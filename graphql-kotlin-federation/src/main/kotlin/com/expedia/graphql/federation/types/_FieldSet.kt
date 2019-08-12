package com.expedia.graphql.federation.types

import com.expedia.graphql.federation.directives.FieldSet
import graphql.Scalars
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType

/**
 * Custom scalar type that is used to represent a set of fields. Grammatically, a field set is a selection set minus the braces.
 * This means it can represent a single field "upc", multiple fields "id countryCode", and even nested selection sets
 * "id organization { id }".
 */
val FIELD_SET_SCALAR_TYPE: GraphQLScalarType = GraphQLScalarType.newScalar(Scalars.GraphQLString)
    .name("_FieldSet")
    .description("Federation type representing set of fields")
    .coercing(FieldSetCoercing)
    .build()

private object FieldSetCoercing : Coercing<FieldSet, String> {
    override fun serialize(input: Any): String = if (input is FieldSet) {
        input.value
    } else {
        throw CoercingSerializeException("Expected type 'FieldSet' but was '${input.javaClass.simpleName}'.")
    }

    override fun parseValue(input: Any): FieldSet = parseLiteral(input)

    override fun parseLiteral(input: Any): FieldSet =
        if (input !is StringValue) {
            throw CoercingParseLiteralException("Expected AST type 'StringValue' but was '${input.javaClass.simpleName}'.")
        } else {
            FieldSet::class.constructors.first().call(input.value)
        }
}
