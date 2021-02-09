plugins {
    id("java")
    id("jacoco")
    id("maven-publish")
    kotlin("jvm") version embeddedKotlinVersion
}

base.archivesBaseName = "okhttp-mock"

dependencies {

    fun compileOnlyAndTests(notation: String) {
        compileOnly(notation)
        testImplementation(notation)
    }

    compileOnlyAndTests("com.squareup.okhttp3:okhttp:4.9.1")
    compileOnlyAndTests("org.robolectric:robolectric:4.5.1")
    compileOnlyAndTests("com.android.support:support-annotations:25.3.1")
    compileOnlyAndTests("com.google.android:android:2.2.1")

    implementation(kotlin("stdlib"))

    testImplementation("junit:junit:4.13.1")
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
    val bintrayPackage = "okhttp-client-mock"
    val bintrayUser = System.getenv("BINTRAY_USER")
    val bintrayKey = System.getenv("BINTRAY_KEY")

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
            name = "local"
            url = file("${rootProject.buildDir}/repo").toURI()
        }
        maven {
            name = "bintray"
            url = uri("https://api.bintray.com/content/$bintrayUser/maven/$bintrayPackage/$version")
            credentials {
                username = bintrayUser
                password = bintrayKey
            }
        }
    }
}
