description = "Spring Boot autoconfiguration library for creating reactive GraphQL server"

plugins {
    id("com.expediagroup.graphql.conventions")
    alias(libs.plugins.kotlin.spring)
    id(libs.plugins.kotlin.kapt.get().pluginId)
}

dependencies {
    api(projects.graphqlKotlinServer)
    api(projects.graphqlKotlinFederation)
    api(libs.spring.boot.webflux)
    api(libs.kotlinx.coroutines.jdk8)
    api(libs.kotlinx.coroutines.reactor)
    api(libs.reactor.extensions)
    api(libs.fastjson2.spring)
    kapt(libs.spring.boot.config)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.spring.boot.test)
    testImplementation(libs.reactor.test)
}

tasks {
    jacocoTestCoverageVerification {
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.82".toBigDecimal()
                }
                limit {
                    counter = "BRANCH"
                    value = "COVEREDRATIO"
                    minimum = "0.67".toBigDecimal()
                }
            }
        }
    }
    val dokkaJavadoc = getByName("dokkaJavadoc") {
        dependsOn("kaptKotlin")
    }
}
