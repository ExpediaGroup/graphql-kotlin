package com.expedia.graphql.directives

import com.expedia.graphql.generator.extensions.getAllDirectives
import com.expedia.graphql.generator.extensions.wireOnEnvironment
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLType
import graphql.schema.idl.SchemaDirectiveWiring

open class KotlinDirectiveWiringFactory(
    private val codeRegistry: GraphQLCodeRegistry.Builder,
    private val manualWiring: Map<String, SchemaDirectiveWiring> = emptyMap()
) {

    /**
     * Wire up the directive based on the GraphQL type
     */
    fun onWire(graphQLType: GraphQLType, parentType: String): GraphQLType {
        if (graphQLType !is GraphQLDirectiveContainer) return graphQLType

        return wireDirectives(graphQLType, parentType, graphQLType.getAllDirectives())
    }

    open fun providesSchemaDirectiveWiring(env: KotlinSchemaDirectiveEnvironment<GraphQLDirectiveContainer>): Boolean = false

    open fun getSchemaDirectiveWiring(env: KotlinSchemaDirectiveEnvironment<GraphQLDirectiveContainer>): SchemaDirectiveWiring? = null

    private fun wireDirectives(element: GraphQLDirectiveContainer, parentType: String, directives: List<GraphQLDirective>): GraphQLDirectiveContainer {
        var modifiedObject = element
        val fieldCoordinates = FieldCoordinates.coordinates(parentType, element.name)

        for (directive in directives) {
            val env = KotlinSchemaDirectiveEnvironment(
                element = modifiedObject,
                coordinates = fieldCoordinates,
                directive = directive,
                codeRegistry = codeRegistry)
            val directiveWiring = discoverWiringProvider(directive.name, env)
            if (directiveWiring != null) {
                modifiedObject = directiveWiring.wireOnEnvironment(env)
            }
        }
        return modifiedObject
    }

    private fun discoverWiringProvider(directiveName: String, env: KotlinSchemaDirectiveEnvironment<GraphQLDirectiveContainer>): SchemaDirectiveWiring? =
        when {
            directiveName in manualWiring -> manualWiring[directiveName]
            providesSchemaDirectiveWiring(env) -> getSchemaDirectiveWiring(env)
            else -> null
        }
}
