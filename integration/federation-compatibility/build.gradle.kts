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
	implementation(libs.kotlin.reflect)
	implementation(libs.kotlin.stdlib)
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
