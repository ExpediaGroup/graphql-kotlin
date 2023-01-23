buildscript {
    repositories {
        mavenCentral()
        google()
        mavenLocal {
            content {
                includeGroup("com.expediagroup")
            }
        }
    }

    dependencies {
        classpath(libs.kotlin.serialization)
        classpath(libs.kotlin.gradle.plugin)
        classpath(libs.android.plugin)
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        mavenLocal {
            content {
                includeGroup("com.expediagroup")
            }
        }
    }
}
