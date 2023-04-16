package com.expediagroup.scalars.types

import com.ibm.icu.util.ULocale
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

val graphqlLocaleType: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("Locale")
    .description("A type representing a Locale such as en_US or fr_FR")
    .coercing(object : Coercing<ULocale, String> {
        override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): ULocale =
            runCatching {
                ULocale(serialize(input, graphQLContext, locale))
            }.getOrElse {
                throw CoercingParseValueException("Expected valid ULocale but was $input")
        }

        override fun parseLiteral(input: Value<*>, variables: CoercedVariables, graphQLContext: GraphQLContext, locale: Locale): ULocale {
            val locale = (input as? StringValue)?.value
            return runCatching {
                ULocale(locale)
            }.getOrElse {
                throw CoercingParseLiteralException("Expected valid ULocale literal but was $locale")
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
