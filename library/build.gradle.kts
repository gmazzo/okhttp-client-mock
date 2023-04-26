import org.jetbrains.kotlin.gradle.utils.extendsFrom

plugins {
    alias(libs.plugins.kotlin)
    `maven-central-publish`
    jacoco
}

description = "A simple OKHttp client mock, using a programmable request interceptor"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

dependencies {
    val compileOnlyAndTests by configurations.creating { isCanBeConsumed = true }
    configurations.compileOnly { extendsFrom(compileOnlyAndTests) }
    configurations.testImplementation { extendsFrom(compileOnlyAndTests) }

    compileOnlyAndTests(libs.okhttp)
    compileOnlyAndTests(libs.android)
    compileOnlyAndTests(libs.android.annotations)
    compileOnlyAndTests(libs.robolectric)

    testImplementation(libs.kotlin.test)
}

publishing.publications {
    create<MavenPublication>("java") { from(components["java"]) }
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}
