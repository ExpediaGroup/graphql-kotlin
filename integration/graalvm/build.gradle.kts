import java.util.Properties

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
