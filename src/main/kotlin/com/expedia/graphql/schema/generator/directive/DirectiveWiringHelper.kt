package com.expedia.graphql.schema.generator.directive

import graphql.Assert.assertNotNull
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLEnumValueDefinition
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLUnionType
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import graphql.schema.idl.SchemaDirectiveWiringEnvironmentImpl
import graphql.schema.idl.WiringFactory

/**
 * Based on
 * https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/schema/idl/SchemaGeneratorDirectiveHelper.java
 */
internal class DirectiveWiringHelper(private val wiringFactory: WiringFactory, private val manualWiring: Map<String, SchemaDirectiveWiring> = mutableMapOf()) {

    /**
     * Wire up the directive based on the GraphQL type
     */
    fun onWire(generatedType: GraphQLType): GraphQLType {
        if (generatedType !is GraphQLDirectiveContainer) return generatedType

        return wireDirectives(
            generatedType,
            getDirectives(generatedType),
            ::createWiringEnvironment,
            getInvoker(generatedType)
        )
    }

    private fun getInvoker(generatedType: GraphQLDirectiveContainer) =
        { wiring: SchemaDirectiveWiring, environment: SchemaDirectiveWiringEnvironment<GraphQLDirectiveContainer> -> wireOnEnvironment(environment, wiring, generatedType) }

    private fun getDirectives(generatedType: GraphQLDirectiveContainer): MutableList<GraphQLDirective> {
        // A function without directives may still be rewired if the arguments have directives
        val directives = generatedType.directives
        if (generatedType is GraphQLFieldDefinition) {
            generatedType.arguments.forEach { directives.addAll(it.directives) }
        }
        return directives
    }

    private fun <T : GraphQLDirectiveContainer> createWiringEnvironment(element: T, directive: GraphQLDirective): SchemaDirectiveWiringEnvironment<T> =
        SchemaDirectiveWiringEnvironmentImpl(element, directive, null, null, null)

    private fun <T : GraphQLDirectiveContainer> wireDirectives(
        element: T,
        directives: List<GraphQLDirective>,
        envBuilder: (T, GraphQLDirective) -> SchemaDirectiveWiringEnvironment<T>,
        invoker: (SchemaDirectiveWiring, SchemaDirectiveWiringEnvironment<T>) -> T
    ): T {
        var outputObject = element
        for (directive in directives) {
            val env = envBuilder.invoke(outputObject, directive)
            val directiveWiring = discoverWiringProvider(directive.name, env)
            if (directiveWiring != null) {
                val newElement = invoker.invoke(directiveWiring, env)
                assertNotNull(newElement, "The SchemaDirectiveWiring MUST return a non null return value for element '" + element.name + "'")
                outputObject = newElement
            }
        }
        return outputObject
    }

    private fun <T : GraphQLDirectiveContainer> discoverWiringProvider(directiveName: String, env: SchemaDirectiveWiringEnvironment<T>): SchemaDirectiveWiring? {
        return if (wiringFactory.providesSchemaDirectiveWiring(env)) {
            wiringFactory.getSchemaDirectiveWiring(env)
        } else {
            manualWiring[directiveName]
        }
    }

    @Suppress("UNCHECKED_CAST", "Detekt.ComplexMethod")
    private fun wireOnEnvironment(environment: SchemaDirectiveWiringEnvironment<GraphQLDirectiveContainer>, wiring: SchemaDirectiveWiring, generatedType: GraphQLDirectiveContainer) =
        when (environment.element) {
            is GraphQLObjectType -> wiring.onObject(environment as SchemaDirectiveWiringEnvironment<GraphQLObjectType>)
            is GraphQLFieldDefinition -> wiring.onField(environment as SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>)
            is GraphQLInterfaceType -> wiring.onInterface(environment as SchemaDirectiveWiringEnvironment<GraphQLInterfaceType>)
            is GraphQLUnionType -> wiring.onUnion(environment as SchemaDirectiveWiringEnvironment<GraphQLUnionType>)
            is GraphQLScalarType -> wiring.onScalar(environment as SchemaDirectiveWiringEnvironment<GraphQLScalarType>)
            is GraphQLEnumType -> wiring.onEnum(environment as SchemaDirectiveWiringEnvironment<GraphQLEnumType>)
            is GraphQLEnumValueDefinition -> wiring.onEnumValue(environment as SchemaDirectiveWiringEnvironment<GraphQLEnumValueDefinition>)
            is GraphQLArgument -> wiring.onArgument(environment as SchemaDirectiveWiringEnvironment<GraphQLArgument>)
            is GraphQLInputObjectType -> wiring.onInputObjectType(environment as SchemaDirectiveWiringEnvironment<GraphQLInputObjectType>)
            is GraphQLInputObjectField -> wiring.onInputObjectField(environment as SchemaDirectiveWiringEnvironment<GraphQLInputObjectField>)
            else -> generatedType
        }
}
