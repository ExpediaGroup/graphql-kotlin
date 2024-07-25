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
                url.set("https://github.com/ExpediaGroup/graphql-kotlin")
                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                organization {
                    name.set("Expedia Group")
                    name.set("https://www.expediagroup.com/")
                }
                developers {
                    developer {
                        name.set("Expedia Group Committers")
                        email.set("oss@expediagroup.com")
                        organization.set("Expedia Group")
                        organizationUrl.set("https://www.expediagroup.com/")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/ExpediaGroup/graphql-kotlin.git")
                    developerConnection.set("scm:git:git://github.com/ExpediaGroup/graphql-kotlin.git")
                    url.set("https://github.com/ExpediaGroup/graphql-kotlin")
                }
            }
        }
    }
}
