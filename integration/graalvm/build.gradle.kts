import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    alias(libs.plugins.graalvm.native) apply false
}

allprojects {
    repositories {
        mavenCentral()
        mavenLocal {
            content {
                includeGroup("com.expediagroup")
            }
        }
    }

    val properties = Properties()
    properties.load(File(rootDir.parentFile.parent, "gradle.properties").inputStream())
    for ((key, value) in properties) {
        if (!project.ext.has(key.toString())) {
            project.ext[key.toString()] = value
        }
    }
    project.version = project.ext["version"]!!
}
