package com.expediagroup.scalars.types

import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.GraphQLScalarType

// We coerce between <String, String> due to a secondary deserialization from Jackson
// see: https://github.com/ExpediaGroup/graphql-kotlin/issues/1220
val graphqlLocaleType: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("Locale")
    .description("A type representing a Locale such as en_US or fr_FR")
    .coercing(object : Coercing<String, String> {
        override fun parseValue(input: Any): String = input.toString()

        override fun parseLiteral(input: Any): String {
            val locale = (input as? StringValue)?.value
            return locale ?: throw CoercingParseLiteralException("Expected valid Locale literal but was $locale")
        }

        override fun serialize(dataFetcherResult: Any): String = dataFetcherResult.toString()
    })
    .build()
