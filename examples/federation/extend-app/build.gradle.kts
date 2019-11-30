plugins {
    id("org.jetbrains.kotlin.plugin.spring")
    id("org.springframework.boot")
}

dependencies {
    implementation(project(path = ":graphql-kotlin-spring-server"))
}
