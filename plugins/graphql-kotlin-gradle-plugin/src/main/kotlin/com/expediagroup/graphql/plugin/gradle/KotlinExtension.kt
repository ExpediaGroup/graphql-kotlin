package com.expediagroup.graphql.plugin.gradle

import org.gradle.api.Project

fun Project.graphql(configure: GraphQLPluginExtension.() -> Unit) =
    extensions.configure(GraphQLPluginExtension::class.java, configure)
