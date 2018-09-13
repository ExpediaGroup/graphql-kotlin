package com.expedia.graphql

import com.expedia.graphql.schema.SchemaConfig
import com.expedia.graphql.schema.SchemaGenerator
import com.expedia.graphql.schema.hooks.NoopSchemaGeneratorHooks
import com.expedia.graphql.schema.hooks.SchemaGeneratorHooks
import graphql.schema.GraphQLSchema

// Entry point to generate a graphql schema using reflection on the passed objects
fun toSchema(
    queries: List<TopLevelObjectDef>,
    mutations: List<TopLevelObjectDef> = emptyList(),
    config: SchemaConfig,
    hooks: SchemaGeneratorHooks = NoopSchemaGeneratorHooks()
): GraphQLSchema = SchemaGenerator(queries, mutations, config, hooks).generate()
