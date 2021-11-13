import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version embeddedKotlinVersion
    jacoco
    signing
    `maven-publish`
    id("org.ajoberstar.grgit") version "4.1.0"
    id("name.remal.maven-publish-nexus-staging") version "1.1.6"
}

dependencies {

    fun compileOnlyAndTests(notation: String) {
        compileOnly(notation)
        testImplementation(notation)
    }

    compileOnlyAndTests("com.squareup.okhttp3:okhttp:4.9.2")
    compileOnlyAndTests("org.robolectric:robolectric:4.6.1")
    compileOnlyAndTests("com.android.support:support-annotations:28.0.0")
    compileOnlyAndTests("com.google.android:android:4.1.1.4")

    implementation(kotlin("stdlib"))

    testImplementation("junit:junit:4.13.2")
}

tasks.withType<JacocoReport> {
    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }

    tasks["check"].dependsOn(this)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = sourceCompatibility

    withJavadocJar()
    withSourcesJar()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = java.sourceCompatibility.toString()
    }
}

signing {
    useGpgCmd()

    sign(publishing.publications)
}

publishing {

    publications.create<MavenPublication>("maven") {
        from(components["java"])
        pom {
            val origin = grgit.remote.list().firstOrNull()?.url

            artifactId = "okhttp-mock"
            name.set("okhttp-client-mock")
            description.set("A simple OKHttp client mock, using a programmable request interceptor")
            url.set(origin)

            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }

            developers {
                developer {
                    id.set("gmazzo")
                    name.set("Guillermo Mazzola")
                    email.set("gmazzo65@gmail.com")
                }
            }

            scm {
                connection.set(origin)
                developerConnection.set(origin)
                url.set(origin)
            }

        }
    }

    repositories {
        fun property(name: String) = sequenceOf(project::findProperty, System::getenv)
            .mapNotNull { it(name)?.toString() }
            .firstOrNull()

        maven(file("${rootProject.buildDir}/repo")) { name = "Local" }
        maven(url = "https://oss.sonatype.org/service/local/staging/deploy/maven2") {
            credentials {
                username = property("ossrhUsername")
                password = property("ossrhPassword")
            }
        }
    }

}

tasks.named("publish") {
    finalizedBy("releaseNexusRepositories")
}
