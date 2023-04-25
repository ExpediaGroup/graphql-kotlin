import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION") // TODO: remove once KTIJ-19369 / Gradle#22797 is fixed
plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-server")
    implementation("com.expediagroup", "graphql-kotlin-hooks-provider")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

// required for maven test
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
