allprojects {
    repositories {
        google()
        jcenter()
    }

    group = "com.github.gmazzo"
    version = "1.3.0"
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
