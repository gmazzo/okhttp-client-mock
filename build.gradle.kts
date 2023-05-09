plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.gradle.nexusPublish)
    `git-versioning`
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl.set(uri("https://oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

allprojects {
    group = "com.github.gmazzo.okhttp.mock"
}
