description = "An example GraphQL Spring server"

plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

val springBootVersion: String by project
val reactorVersion: String by project

dependencies {
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("io.projectreactor:reactor-test:$reactorVersion")
}
