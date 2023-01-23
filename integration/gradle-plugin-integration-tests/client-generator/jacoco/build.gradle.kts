import com.expediagroup.graphql.plugin.gradle.graphql

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    id("com.expediagroup.graphql")
    alias(libs.plugins.kotlin.jvm)
    jacoco
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-spring-client")
    testImplementation(libs.junit.api)
    testImplementation(libs.junit.engine)
    testImplementation(libs.mockk)
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
