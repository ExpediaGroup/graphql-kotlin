package com.expediagroup.scalars.types

import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import java.util.Locale
import java.util.UUID

val graphqlUUIDType: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("UUID")
    .description("A type representing a formatted java.util.UUID")
    .coercing(object : Coercing<UUID, String> {
        override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): UUID =
            runCatching {
                UUID.fromString(serialize(input, graphQLContext, locale))
            }.getOrElse {
                throw CoercingParseValueException("Expected valid UUID but was $input")
            }

        override fun parseLiteral(input: Value<*>, variables: CoercedVariables, graphQLContext: GraphQLContext, locale: Locale): UUID {
            val uuidString = (input as? StringValue)?.value
            return runCatching {
                UUID.fromString(uuidString)
            }.getOrElse {
                throw CoercingParseLiteralException("Expected valid UUID literal but was $uuidString")
            }
        }

        override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): String =
            runCatching {
                dataFetcherResult.toString()
            }.getOrElse {
                throw CoercingSerializeException("Data fetcher result $dataFetcherResult cannot be serialized to a String")
            }
    })
    .build()
