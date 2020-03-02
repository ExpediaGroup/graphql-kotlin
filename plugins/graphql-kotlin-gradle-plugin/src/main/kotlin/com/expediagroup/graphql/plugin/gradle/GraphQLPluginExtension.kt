package com.expediagroup.graphql.plugin.gradle

import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty

@Suppress("UnstableApiUsage")
open class GraphQLPluginExtension(project: Project) {
    var endpoint: String? = null
    var sdlEndpoint: String? = null
    var schemaFileName: String? = null
    var schemaFile: RegularFileProperty = project.objects.fileProperty()
    var packageName: String? = null
    var queryFileDirectory: String? = null
}
