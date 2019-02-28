plugins {
    id("java")
    id("jacoco")
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm") version "1.3.21"
    id("com.jfrog.bintray") version "1.8.4"
}

base.archivesBaseName = "okhttp-mock"

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies {
    compileOnly("com.squareup.okhttp3:okhttp:3.8.1")
    compileOnly("org.robolectric:robolectric:3.4.2")
    compileOnly("com.android.support:support-annotations:25.3.1")
    compileOnly("com.google.android:android:2.2.1")

    implementation(kotlin("stdlib"))

    testImplementation(configurations.compileOnly)
    testImplementation("junit:junit:4.12")
}

tasks.withType(JacocoReport::class.java) {
    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }

    tasks["check"].dependsOn(this)
}

val sourcesJar by tasks.creating(Jar::class) {
    dependsOn(JavaPlugin.CLASSES_TASK_NAME)
    classifier = "sources"
    from(sourceSets["main"].allSource)
}

val javadocJar by tasks.creating(Jar::class) {
    dependsOn(JavaPlugin.JAVADOC_TASK_NAME)
    classifier = "javadoc"
    from(sourceSets["main"].allSource)
}

val repoDesc = "A simple OKHttp client mock, using a programmable request interceptor Edit"
val repoName = "okhttp-client-mock"
val repoUrl = "https://github.com/gmazzo/$repoName"
val repoTags = arrayOf("okhttp", "mock", "retrofit", "test")

publishing {
    publications {
        create<MavenPublication>("default") {
            artifactId = base.archivesBaseName
            from(components["java"])
            artifact(sourcesJar)
            artifact(javadocJar)
        }
    }
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_KEY")
    pkg = PackageConfig().apply {
        repo = "maven"
        name = repoName
        desc = repoDesc
        vcsUrl = "${repoUrl}.git"
        githubRepo = repoUrl.replace("https://github.com/", "")
        githubReleaseNotesFile = "README.md"
        publicDownloadNumbers = true
        setLicenses("MIT")
        setLabels(*repoTags)
    }

    setPublications("default")
}
