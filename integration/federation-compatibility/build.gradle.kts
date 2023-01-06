import com.expediagroup.graphql.plugin.gradle.graphql
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenCentral()
        mavenLocal {
            content {
                includeGroup("com.expediagroup")
            }
        }
    }
}

plugins {
    id("com.expediagroup.graphql")
    id("org.springframework.boot") version "2.7.7"
	kotlin("jvm") version "1.7.21"
	kotlin("plugin.spring") version "1.7.21"
}

group = "com.expediagroup.federation.compatibility"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
    mavenLocal {
        content {
            includeGroup("com.expediagroup")
        }
    }
}

dependencies {
	implementation("org.jetbrains.kotlin", "kotlin-reflect")
	implementation("org.jetbrains.kotlin", "kotlin-stdlib-jdk8")
	implementation("com.expediagroup", "graphql-kotlin-spring-server")
    graphqlSDL("com.expediagroup", "graphql-kotlin-federated-hooks-provider")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

graphql {
    schema {
        packages = listOf("com.expediagroup.federation.compatibility")
    }
}
