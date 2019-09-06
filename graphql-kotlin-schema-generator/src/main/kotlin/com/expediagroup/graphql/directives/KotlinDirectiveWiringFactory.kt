package com.expediagroup.graphql.directives

import com.expediagroup.graphql.exceptions.InvalidSchemaDirectiveWiringException
import com.expediagroup.graphql.generator.extensions.getAllDirectives
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLType

/**
 * Wiring factory that is used to provide the directives.
 */
open class KotlinDirectiveWiringFactory(
    private val manualWiring: Map<String, KotlinSchemaDirectiveWiring> = emptyMap()
) {

    /**
     * Wire up the directive based on the GraphQL type.
     */
    fun onWire(graphQLType: GraphQLType, coordinates: FieldCoordinates? = null, codeRegistry: GraphQLCodeRegistry.Builder? = null): GraphQLType {
        if (graphQLType !is GraphQLDirectiveContainer) return graphQLType

        return wireDirectives(graphQLType, coordinates, graphQLType.getAllDirectives(), codeRegistry)
    }

    /**
     * Retrieve schema directive wiring for the specified environment or NULL if wiring is not supported by this factory.
     */
    open fun getSchemaDirectiveWiring(environment: KotlinSchemaDirectiveEnvironment<GraphQLDirectiveContainer>): KotlinSchemaDirectiveWiring? = null

    @Suppress("Detekt.ThrowsCount")
    private fun wireDirectives(
        element: GraphQLDirectiveContainer,
        coordinates: FieldCoordinates?,
        directives: List<GraphQLDirective>,
        codeRegistry: GraphQLCodeRegistry.Builder?
    ): GraphQLDirectiveContainer {
        var modifiedObject = element
        for (directive in directives) {
            val env = if (modifiedObject is GraphQLFieldDefinition) {
                KotlinFieldDirectiveEnvironment(
                    field = modifiedObject,
                    fieldDirective = directive,
                    coordinates = coordinates ?: throw InvalidSchemaDirectiveWiringException("Unable to wire directive on a field due to missing field coordinates"),
                    codeRegistry = codeRegistry ?: throw InvalidSchemaDirectiveWiringException("Unable to wire directive on a field due to a missing code registry"))
            } else {
                KotlinSchemaDirectiveEnvironment(
                    element = modifiedObject,
                    directive = directive)
            }

            if (!env.isValid()) {
                throw InvalidSchemaDirectiveWiringException(
                    "Directive ${directive.name} not applicable on specified ${element.name} GraphQLType, valid directive locations ${directive.validLocations()}")
            }

            val directiveWiring = discoverWiringProvider(directive.name, env)
            if (directiveWiring != null) {
                modifiedObject = directiveWiring.wireOnEnvironment(env)
            }
        }
        return modifiedObject
    }

    private fun discoverWiringProvider(directiveName: String, env: KotlinSchemaDirectiveEnvironment<GraphQLDirectiveContainer>): KotlinSchemaDirectiveWiring? =
        if (directiveName in manualWiring) {
            manualWiring[directiveName]
        } else {
            getSchemaDirectiveWiring(env)
        }
}
