plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
}

repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
}

dependencies {
    // add plugin artifacts so we can reference them in plugins block in the precompiled script
    // in the future maybe we could update below to <plugin id>:<plugin id>.gradle.plugin:<plugin version> coordinates
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.detekt.plugin)
    implementation(libs.dokka.plugin)
    implementation(libs.ktlint.plugin)

    // this is a workaround to enable version catalog usage in the convention plugin
    // see https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
