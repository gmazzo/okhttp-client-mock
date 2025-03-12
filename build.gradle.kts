plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.axion.release)
    alias(libs.plugins.publicationsReport)
}

allprojects {
    group = "com.github.gmazzo.okhttp.mock"
    version = rootProject.scmVersion.version

    tasks.withType<JacocoReport>().configureEach {
        reports.xml.required = true
    }
}
