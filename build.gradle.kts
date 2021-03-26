allprojects {

    repositories {
        google()
        mavenCentral()
    }

    group = "com.github.gmazzo"
    version = "1.4.1"
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
