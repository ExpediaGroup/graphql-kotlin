package com.expediagroup.graphql.plugin.gradle

import org.gradle.api.Project

open class GraphQLPluginExtension(project: Project) {
    var endpoint: String? = null
    var sdlEndpoint: String? = null
    var schemaFile: String? = null
}
