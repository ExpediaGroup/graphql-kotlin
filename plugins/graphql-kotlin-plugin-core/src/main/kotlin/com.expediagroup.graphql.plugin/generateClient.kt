package com.expediagroup.graphql.plugin

import com.expediagroup.graphql.plugin.generator.GraphQLClientGenerator
import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorConfig
import com.squareup.kotlinpoet.FileSpec
import graphql.schema.idl.SchemaParser
import java.io.File

fun generateClient(packageName: String, schema: File, queries: List<File>): List<FileSpec> {
    val graphQLSchema = SchemaParser().parse(schema)

    val config = GraphQLClientGeneratorConfig(packageName = packageName)
    val generator = GraphQLClientGenerator(graphQLSchema, config)
    return queries.map { queryFile ->
        generator.generate(queryFile)
    }
}
