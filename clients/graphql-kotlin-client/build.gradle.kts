description = "A lightweight typesafe GraphQL HTTP Client"

plugins {
    id("com.expediagroup.graphql.conventions")
}

dependencies {
    api(libs.kotlinx.coroutines.core)
}
