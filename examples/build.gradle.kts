allprojects {
    buildscript {
        repositories {
            mavenCentral()
            mavenLocal {
                content {
                    includeGroup("com.expediagroup")
                }
            }
        }
    }

    repositories {
        mavenCentral()
        mavenLocal {
            content {
                includeGroup("com.expediagroup")
            }
        }
    }
}
