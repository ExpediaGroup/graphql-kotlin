package com.expediagroup.graphql.federation.types

import graphql.Assert
import graphql.language.ArrayValue
import graphql.language.BooleanValue
import graphql.language.EnumValue
import graphql.language.FloatValue
import graphql.language.IntValue
import graphql.language.NullValue
import graphql.language.ObjectField
import graphql.language.ObjectValue
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import java.util.stream.Collectors

/**
 * The _Any scalar is used to pass representations of entities from external services into the root _entities field for execution.
 * Validation of the _Any scalar is done by matching the __typename and @external fields defined in the schema.
 */
val ANY_SCALAR_TYPE: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("_Any")
    .description("Federation scalar type used to represent any external entities passed to _entities query.")
    .coercing(AnyCoercing)
    .build()

private object AnyCoercing : Coercing<Any, Any> {

    override fun serialize(dataFetcherResult: Any): Any = dataFetcherResult

    override fun parseValue(input: Any): Any = input

    @Suppress("ComplexMethod")
    override fun parseLiteral(input: Any): Any? =
        when (input) {
            is NullValue -> null
            is FloatValue -> input.value
            is StringValue -> input.value
            is IntValue -> input.value
            is BooleanValue -> input.isValue
            is EnumValue -> input.name
            is ArrayValue -> input.values
                .stream()
                .map { parseLiteral(it) }
                .collect(Collectors.toList())
            is ObjectValue -> input.objectFields
                .stream()
                .collect(Collectors.toMap(ObjectField::getName) { parseLiteral(it.value) })
            else -> Assert.assertShouldNeverHappen<Any>()
        }
}
