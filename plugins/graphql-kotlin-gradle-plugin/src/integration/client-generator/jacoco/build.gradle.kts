import com.expediagroup.graphql.plugin.gradle.graphql

buildscript {
    repositories {
        mavenLocal {
            content {
                includeGroup("com.expediagroup")
            }
        }
    }

    val graphQLKotlinVersion = System.getenv("GRAPHQL_KOTLIN_VERSION") ?: "7.0.0-SNAPSHOT"
    dependencies {
        classpath("com.expediagroup:graphql-kotlin-gradle-plugin:$graphQLKotlinVersion")
    }
}

plugins {
    kotlin("jvm") version "1.7.21"
    jacoco
}

apply(plugin = "com.expediagroup.graphql")

repositories {
    mavenCentral()
    mavenLocal {
        content {
            includeGroup("com.expediagroup")
        }
    }
}

val graphQLKotlinVersion = System.getenv("GRAPHQL_KOTLIN_VERSION") ?: "7.0.0-SNAPSHOT"
val junitVersion = System.getenv("JUNIT_VERSION") ?: "5.8.2"
val mockkVersion = System.getenv("MOCKK_VERSION") ?: "1.11.0"
dependencies {
    implementation("com.expediagroup:graphql-kotlin-spring-client:$graphQLKotlinVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("io.mockk:mockk:1.11.0")
}

graphql {
    client {
        schemaFile = file("${project.projectDir}/schema.graphql")
        packageName = "com.expediagroup.jacoco.generated"
    }
}

tasks {
    check {
        dependsOn(jacocoTestCoverageVerification)
    }
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.99".toBigDecimal()
                }
            }
        }
    }
    test {
        useJUnitPlatform()
        finalizedBy(jacocoTestReport)
    }
}
