plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    fun plugin(dep: Provider<PluginDependency>) = with(dep.get()) {
        create("$pluginId:$pluginId.gradle.plugin:$version")
    }

    implementation(plugin(libs.plugins.dokka))
}
