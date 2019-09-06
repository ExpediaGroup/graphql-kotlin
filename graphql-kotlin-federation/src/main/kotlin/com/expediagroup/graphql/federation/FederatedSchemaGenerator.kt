package com.expediagroup.graphql.federation

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.federation.directives.ExtendsDirective
import com.expediagroup.graphql.generator.SchemaGenerator
import graphql.schema.GraphQLSchema
import org.reflections.Reflections
import kotlin.reflect.full.createType

/**
 * Generates federated GraphQL schemas based on the specified configuration.
 */
class FederatedSchemaGenerator(generatorConfig: FederatedSchemaGeneratorConfig) : SchemaGenerator(generatorConfig) {

    override fun generate(
        queries: List<TopLevelObject>,
        mutations: List<TopLevelObject>,
        subscriptions: List<TopLevelObject>,
        builder: GraphQLSchema.Builder
    ): GraphQLSchema {
        builder.federation(config.supportedPackages)
        return super.generate(queries, mutations, subscriptions, builder)
    }

    /**
     * Scans specified packages for all the federated (extended) types and adds them to the target schema.
     */
    fun GraphQLSchema.Builder.federation(supportedPackages: List<String>): GraphQLSchema.Builder {
        supportedPackages
            .map { pkg -> Reflections(pkg).getTypesAnnotatedWith(ExtendsDirective::class.java).map { it.kotlin } }
            .flatten()
            .map {
                val graphQLType = if (it.isAbstract) {
                    interfaceType(it)
                } else {
                    objectType(it)
                }

                // workaround to explicitly apply validation
                config.hooks.didGenerateGraphQLType(it.createType(), graphQLType)
            }
            .forEach {
                this.additionalType(it)
            }
        return this
    }
}
