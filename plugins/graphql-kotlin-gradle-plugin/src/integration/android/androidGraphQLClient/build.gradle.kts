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

    val graphQLKotlinVersion = System.getenv("GRAPHQL_KOTLIN_VERSION") ?: "5.0.0-SNAPSHOT"
    val kotlinVersion = System.getenv("KOTLIN_VERSION") ?: "1.5.21"
    dependencies {
        classpath("com.android.tools.build:gradle:4.2.2")
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
