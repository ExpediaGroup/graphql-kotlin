description = "A lightweight typesafe GraphQL HTTP Client"

val kotlinCoroutinesVersion: String by project
val springVersion: String by project
val springBootVersion: String by project
val wireMockVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-client"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$kotlinCoroutinesVersion")
    api("org.springframework:spring-webflux:$springVersion")
    api("org.springframework.boot:spring-boot-starter-reactor-netty:$springBootVersion")
    testImplementation("com.github.tomakehurst:wiremock-jre8:$wireMockVersion")
}
