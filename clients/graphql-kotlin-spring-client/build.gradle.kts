description = "A lightweight typesafe GraphQL HTTP Client based on Spring WebClient"

plugins {
    id("com.expediagroup.graphql.conventions")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    api(projects.graphqlKotlinClient)
    api(projects.graphqlKotlinClientJackson)
    api(libs.kotlinx.coroutines.reactor)
    api(libs.spring.webflux)
    api(libs.spring.context)
    api(libs.spring.boot.netty)
    testImplementation(projects.graphqlKotlinClientSerialization)
    testImplementation(libs.wiremock.lib)
}

tasks {
    jacocoTestCoverageVerification {
        // Exclude synthetic lambda classes inlined from Spring's awaitBody (WebClientExtensions.kt).
        // Because awaitBody is a `suspend inline` function in Spring 7, the Kotlin compiler inlines
        // its body (including an anonymous Function2 lambda from the withContext block) directly into
        // GraphQLWebClient's bytecode. JaCoCo instruments these inlined instructions but cannot find
        // their source file (it lives in the Spring JAR), causing false coverage misses.
        classDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                exclude("**/GraphQLWebClient\$execute\$suspendImpl\$\$inlined\$awaitBody\$*")
            }
        )
        violationRules {
            rule {
                limit {
                    counter = "INSTRUCTION"
                    value = "COVEREDRATIO"
                    minimum = "0.96".toBigDecimal()
                }
            }
        }
    }
}
