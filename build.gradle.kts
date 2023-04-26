plugins {
    alias(libs.plugins.kotlin) apply false
    alias(libs.plugins.gitVersioning)
    alias(libs.plugins.gradle.nexusPublish)
}

gitVersioning.apply {
    refs {
        branch(".+") {
            describeTagPattern = "v(?<version>.*)"
            version = "\${describe.tag.version}-SNAPSHOT"
        }
        tag("v(?<version>.*)") {
            version = "\${ref.version}"
        }
    }
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
