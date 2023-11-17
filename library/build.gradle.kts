import org.gradle.api.publish.maven.internal.publication.MavenPomInternal
import org.gradle.api.publish.maven.internal.publication.MavenPublicationInternal
import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.utils.extendsFrom

plugins {
    alias(libs.plugins.kotlin)
    `maven-central-publish`
    jacoco
}

description = "A simple OKHttp client mock, using a programmable request interceptor"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

val legacyPOM by configurations.creating

dependencies {
    val compileOnlyAndTests by configurations.creating { isCanBeConsumed = true }
    configurations.compileOnly.configure { extendsFrom(compileOnlyAndTests) }
    configurations.testImplementation.configure { extendsFrom(compileOnlyAndTests) }

    compileOnlyAndTests(libs.okhttp)
    compileOnlyAndTests(libs.android)
    compileOnlyAndTests(libs.android.annotations)
    compileOnlyAndTests(libs.robolectric)

    testImplementation(libs.kotlin.test)

    legacyPOM(project)
}

publishing.publications {
    create<MavenPublication>("java") { from(components["java"]) }

    create<MavenPublication>("legacy") {
        from(serviceOf<SoftwareComponentFactory>().adhoc("legacy").apply {
            addVariantsFromConfiguration(legacyPOM) {
                mapToMavenScope("compile")
            }
        })
        groupId = "com.github.gmazzo"
        artifactId = "okhttp-mock"
        (this as MavenPublicationInternal).isAlias = true
        pom {
            name.set("${project.name} (deprecated)")
            description.set("Deprecated: replaced by com.github.gmazzo.okhttp.mock:mock-client:$version")
        }
    }
}

tasks.named("generateMetadataFileForLegacyPublication") {
    enabled = false
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}
