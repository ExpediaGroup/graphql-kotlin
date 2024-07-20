description = "BOM (Bill Of Materials) for graphql-kotlin"

plugins {
    `maven-publish`
    `java-platform`
}

javaPlatform {
    allowDependencies()
}

dependencies {
    project.rootProject.subprojects.forEach { subproject ->
        if (subproject.name != "graphql-kotlin-bom") {
            api(subproject)
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("graphql-kotlin-bom") {
            from(components["javaPlatform"])
            pom {
                description = "BOM (Bill Of Materials) for graphql-kotlin"
                name = "graphql-kotlin-bom"
                packaging = "pom"
            }
        }
    }
}
