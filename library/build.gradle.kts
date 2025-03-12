plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.dokka)
    alias(libs.plugins.mavenPublish)
    jacoco
}

description = "A simple OKHttp client mock, using a programmable request interceptor"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

dependencies {
    val compileOnlyAndTests by configurations.creating { isCanBeConsumed = true }
    configurations.compileOnly.configure { extendsFrom(compileOnlyAndTests) }
    configurations.testImplementation.configure { extendsFrom(compileOnlyAndTests) }

    compileOnlyAndTests(libs.okhttp)
    compileOnlyAndTests(libs.android)
    compileOnlyAndTests(libs.android.annotations)
    compileOnlyAndTests(libs.robolectric)

    testImplementation(libs.kotlin.test)
}

val originUrl = providers
    .exec { commandLine("git", "remote", "get-url", "origin") }
    .standardOutput.asText.map { it.trim() }

mavenPublishing {
    publishToMavenCentral("CENTRAL_PORTAL", automaticRelease = true)

    pom {
        name = "${rootProject.name}-${project.name}"
        description = provider { project.description }
        url = originUrl

        licenses {
            license {
                name = "MIT License"
                url = "https://opensource.org/license/mit/"
            }
        }

        developers {
            developer {
                id = "gmazzo"
                name = id
                email = "gmazzo65@gmail.com"
            }
        }

        scm {
            connection = originUrl
            developerConnection = originUrl
            url = originUrl
        }
    }
}

// relocation POM for legacy coordinates
afterEvaluate {
    publishing.publications {
        val maven = named<MavenPublication>("maven")

        register<MavenPublication>("legacy") {
            groupId = "com.github.gmazzo"
            artifactId = "okhttp-mock"

            pom {
                name = maven.map { "$it (deprecated)" }
                description = maven.map { "Deprecated: replaced by ${it.groupId}:${it.artifactId}" }
                distributionManagement {
                    relocation {
                        groupId = maven.map { it.groupId }
                        artifactId = maven.map { it.artifactId }
                    }
                }
            }
        }
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}
