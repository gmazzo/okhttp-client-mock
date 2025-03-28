plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.gitVersion)
    alias(libs.plugins.publicationsReport)
}

allprojects {
    group = "com.github.gmazzo.okhttp.mock"

    tasks.withType<JacocoReport>().configureEach {
        reports.xml.required = true
    }
}
