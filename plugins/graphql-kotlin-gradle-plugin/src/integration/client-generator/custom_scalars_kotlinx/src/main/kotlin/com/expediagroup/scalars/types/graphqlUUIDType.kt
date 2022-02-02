package com.expediagroup.scalars.types

import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import java.util.UUID

val graphqlUUIDType: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("UUID")
    .description("A type representing a formatted java.util.UUID")
    .coercing(object : Coercing<UUID, String> {
        override fun parseValue(input: Any): UUID = runCatching {
            UUID.fromString(serialize(input))
        }.getOrElse {
            throw CoercingParseValueException("Expected valid UUID but was $input")
        }

        override fun parseLiteral(input: Any): UUID {
            val uuidString = (input as? StringValue)?.value
            return runCatching {
                UUID.fromString(uuidString)
            }.getOrElse {
                throw CoercingParseLiteralException("Expected valid UUID literal but was $uuidString")
            }
        }

        override fun serialize(dataFetcherResult: Any): String = runCatching {
            dataFetcherResult.toString()
        }.getOrElse {
            throw CoercingSerializeException("Data fetcher result $dataFetcherResult cannot be serialized to a String")
        }
    })
    .build()
