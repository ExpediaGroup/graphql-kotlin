package com.expediagroup.graphql.directives

import graphql.introspection.Introspection
import graphql.schema.DataFetcher
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLCodeRegistry
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
import graphql.schema.GraphQLUnionType

/**
 * KotlinSchemaDirectiveEnvironment holds basic wiring information that includes target element and directive that is to be applied.
 */
open class KotlinSchemaDirectiveEnvironment<out T : GraphQLDirectiveContainer>(
    val element: T,
    val directive: GraphQLDirective
) {
    /**
     * Verifies whether specified directive is applicable on the target element.
     */
    @Suppress("Detekt.ComplexMethod")
    fun isValid(): Boolean =
        when (element) {
            is GraphQLArgument -> directive.validLocations().contains(Introspection.DirectiveLocation.ARGUMENT_DEFINITION)
            is GraphQLEnumType -> directive.validLocations().contains(Introspection.DirectiveLocation.ENUM)
            is GraphQLEnumValueDefinition -> directive.validLocations().contains(Introspection.DirectiveLocation.ENUM_VALUE)
            is GraphQLFieldDefinition -> directive.validLocations().contains(Introspection.DirectiveLocation.FIELD_DEFINITION)
            is GraphQLInputObjectField -> directive.validLocations().contains(Introspection.DirectiveLocation.INPUT_FIELD_DEFINITION)
            is GraphQLInputObjectType -> directive.validLocations().contains(Introspection.DirectiveLocation.INPUT_OBJECT)
            is GraphQLInterfaceType -> directive.validLocations().contains(Introspection.DirectiveLocation.INTERFACE)
            is GraphQLObjectType -> directive.validLocations().contains(Introspection.DirectiveLocation.OBJECT)
            is GraphQLScalarType -> directive.validLocations().contains(Introspection.DirectiveLocation.SCALAR)
            is GraphQLUnionType -> directive.validLocations().contains(Introspection.DirectiveLocation.UNION)
            else -> false
        }
}

/**
 * KotlinFieldDirectiveEnvironment holds wiring information for applying directives on GraphQL fields.
 */
class KotlinFieldDirectiveEnvironment(
    field: GraphQLFieldDefinition,
    fieldDirective: GraphQLDirective,
    private val coordinates: FieldCoordinates,
    private val codeRegistry: GraphQLCodeRegistry.Builder
) : KotlinSchemaDirectiveEnvironment<GraphQLFieldDefinition>(element = field, directive = fieldDirective) {

    /**
     * Retrieve current data fetcher associated with the target element.
     */
    fun getDataFetcher(): DataFetcher<Any> = codeRegistry.getDataFetcher(coordinates, element)

    /**
     * Update target element data fetcher.
     */
    fun setDataFetcher(newDataFetcher: DataFetcher<Any>) {
        codeRegistry.dataFetcher(coordinates, newDataFetcher)
    }
}
