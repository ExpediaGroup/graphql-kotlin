package com.expedia.graphql

import com.expedia.graphql.schema.SchemaConfig
import com.expedia.graphql.schema.SchemaGenerator
import com.expedia.graphql.schema.hooks.NoopSchemaGeneratorHooks
import com.expedia.graphql.schema.hooks.SchemaGeneratorHooks
import graphql.schema.GraphQLSchema

/**
 * Entry point to generate a graphql schema using reflection on the passed objects.
 *
 * @param queries   List of [TopLevelObjectDef] objects to use for GraphQL queries
 * @param mutations List of [TopLevelObjectDef] objects to use for GraphQL mutations
 * @param config    Schema generation configuration
 * @param hooks     Client specified callbacks for parts of the generation lifecycle
 */
fun toSchema(
    queries: List<TopLevelObjectDef>,
    mutations: List<TopLevelObjectDef> = emptyList(),
    config: SchemaConfig,
    hooks: SchemaGeneratorHooks = NoopSchemaGeneratorHooks()
): GraphQLSchema = SchemaGenerator(queries, mutations, config, hooks).generate()
