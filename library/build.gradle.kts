plugins {
    id("java")
    id("jacoco")
    id("maven-publish")
}

base.archivesBaseName = "okhttp-mock"

dependencies {
    compileOnly("com.squareup.okhttp3:okhttp:3.8.1")
    compileOnly("org.robolectric:robolectric:3.4.2")
    compileOnly("com.android.support:support-annotations:25.3.1")
    compileOnly("com.google.android:android:2.2.1")

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

publishing {
    publications {
        create<MavenPublication>("default") {
            artifactId = base.archivesBaseName
            from(components["java"])
            artifact(sourcesJar)
            artifact(javadocJar)
        }
    }
    repositories {
        maven {
            name = "bintray"
            url = uri("https://api.bintray.com/content/${System.getenv("BINTRAY_USER")}/maven/okhttp-client-mock/$version")
            credentials {
                username = System.getenv("BINTRAY_USER")
                password = System.getenv("BINTRAY_KEY")
            }
        }
    }
}
