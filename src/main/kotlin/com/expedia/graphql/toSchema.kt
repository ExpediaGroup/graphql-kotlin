package com.expedia.graphql

import com.expedia.graphql.schema.SchemaGenerator
import com.expedia.graphql.schema.SchemaGeneratorConfig
import graphql.schema.GraphQLSchema

/**
 * Entry point to generate a graphql schema using reflection on the passed objects.
 *
 * @param queries   List of [TopLevelObjectDef] objects to use for GraphQL queries
 * @param mutations List of [TopLevelObjectDef] objects to use for GraphQL mutations
 * @param config    Schema generation configuration
 */
fun toSchema(
    queries: List<TopLevelObjectDef>,
    mutations: List<TopLevelObjectDef> = emptyList(),
    config: SchemaGeneratorConfig
): GraphQLSchema = SchemaGenerator(queries, mutations, config).generate()
