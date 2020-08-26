plugins {
    id("org.jetbrains.kotlin.jvm") version "1.4.0"
    id("org.jetbrains.kotlin.plugin.spring") version "1.4.0"
    id("org.springframework.boot") version "2.2.7.RELEASE"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("com.expediagroup:graphql-kotlin-spring-server:3.1.0")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs = listOf("-Xjsr305=strict")

        }
    }
}
