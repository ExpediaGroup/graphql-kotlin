import com.expediagroup.graphql.plugin.gradle.graphql
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
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
        kotlinOptions {
            jvmTarget = "17"
            freeCompilerArgs = listOf("-Xjsr305=strict")
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
