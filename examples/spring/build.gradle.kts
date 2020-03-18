plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
}

val springBootVersion: String by project
val reactorVersion: String by project

dependencies {
    implementation("com.expediagroup:graphql-kotlin-spring-server")
    testImplementation("org.springframework.boot:spring-boot-starter-test:$springBootVersion")
    testImplementation("io.projectreactor:reactor-test:$reactorVersion")
}
