val jacksonVersion: String by project

dependencies {
    api(project(path = ":graphql-kotlin-client"))
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
}
