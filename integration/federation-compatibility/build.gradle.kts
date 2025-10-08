import com.expediagroup.graphql.plugin.gradle.graphql
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.expediagroup.graphql")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
}

group = "com.expediagroup.federation.compatibility"

repositories {
    mavenCentral()
    mavenLocal {
        content {
            includeGroup("com.expediagroup")
        }
    }
}

dependencies {
	implementation(libs.kotlin.reflect)
	implementation(libs.kotlin.stdlib)
	implementation("com.expediagroup", "graphql-kotlin-spring-server")
    graphqlSDL("com.expediagroup", "graphql-kotlin-federated-hooks-provider")
}

tasks {
    kotlin {
        jvmToolchain(17)
    }
    withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
            freeCompilerArgs.set(listOf("-Xjsr305=strict"))
        }
    }
    withType<Test> {
        useJUnitPlatform()
    }
}

graphql {
    schema {
        packages = listOf("com.expediagroup.federation.compatibility")
    }
}
