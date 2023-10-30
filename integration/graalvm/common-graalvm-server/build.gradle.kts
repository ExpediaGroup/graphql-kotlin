plugins {
    alias(libs.plugins.kotlin.jvm)
    `maven-publish`
}

dependencies {
    implementation("com.expediagroup", "graphql-kotlin-server")
    implementation("com.expediagroup", "graphql-kotlin-hooks-provider")
}

tasks {
    kotlin {
        jvmToolchain(17)
    }
}

// required for maven test
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
