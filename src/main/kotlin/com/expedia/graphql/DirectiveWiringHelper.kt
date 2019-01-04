package com.expedia.graphql

import com.expedia.graphql.exceptions.InvalidSchemaDirectiveWiringException
import com.expedia.graphql.generator.extensions.getAllDirectives
import com.expedia.graphql.generator.extensions.wireOnEnvironment
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLType
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import graphql.schema.idl.SchemaDirectiveWiringEnvironmentImpl
import graphql.schema.idl.WiringFactory

/**
 * Based on SchemaGeneratorDirectiveHelper from graphql-java.
 *
 * Relies on [WiringFactory] for providing applicable [SchemaDirectiveWiring].
 * If manual directive wiring map is provided it will take precedence over the wiring factory.
 */
class DirectiveWiringHelper(
    private val wiringFactory: WiringFactory? = null,
    private val manualWiring: Map<String, SchemaDirectiveWiring> = mapOf()
) {

    /**
     * Wire up the directive based on the GraphQL type
     */
    fun onWire(graphQLType: GraphQLType): GraphQLType {
        if (graphQLType !is GraphQLDirectiveContainer) return graphQLType

        return wireDirectives(graphQLType, graphQLType.getAllDirectives())
    }

    private fun wireDirectives(element: GraphQLDirectiveContainer, directives: List<GraphQLDirective>): GraphQLDirectiveContainer {
        var outputObject = element

        for (directive in directives) {
            // we are only specifying element and directive, other fields are not used by graphql-kotlin
            val env = SchemaDirectiveWiringEnvironmentImpl(outputObject, directive, null, null, null)
            val directiveWiring = discoverWiringProvider(directive.name, env)
            if (directiveWiring != null) {
                val newElement = directiveWiring.wireOnEnvironment(env)
                    ?: throw InvalidSchemaDirectiveWiringException(element.name)

                outputObject = newElement
            }
        }

        return outputObject
    }

    private fun discoverWiringProvider(directiveName: String, env: SchemaDirectiveWiringEnvironment<GraphQLDirectiveContainer>): SchemaDirectiveWiring? =
        when {
            directiveName in manualWiring -> manualWiring[directiveName]
            true == wiringFactory?.providesSchemaDirectiveWiring(env) -> wiringFactory.getSchemaDirectiveWiring(env)
            else -> null
        }
}
