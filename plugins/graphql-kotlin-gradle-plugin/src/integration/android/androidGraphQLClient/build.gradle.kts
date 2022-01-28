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

    val androidPluginVersion = System.getenv("ANDROID_PLUGIN_VERSION") ?: "7.0.1"
    val graphQLKotlinVersion = System.getenv("GRAPHQL_KOTLIN_VERSION") ?: "6.0.0-SNAPSHOT"
    val kotlinVersion = System.getenv("KOTLIN_VERSION") ?: "1.5.31"
    dependencies {
        classpath("com.android.tools.build:gradle:$androidPluginVersion")
        classpath("com.expediagroup:graphql-kotlin-gradle-plugin:$graphQLKotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
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
