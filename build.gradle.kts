allprojects {
    repositories {
        google()
        jcenter()
    }

    group = "com.github.gmazzo"
    version = "1.2.1"
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
