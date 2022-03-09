/*
 * Copyright 2021 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.examples.client.server.scalars

import com.ibm.icu.util.ULocale
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType

internal val graphqlULocaleType = GraphQLScalarType.newScalar()
    .name("Locale")
    .description("A type representing a Locale such as en_US or fr_FR")
    .coercing(ULocaleCoercing)
    .build()

private object ULocaleCoercing : Coercing<ULocale, String> {
    override fun parseValue(input: Any): ULocale = runCatching {
        ULocale(serialize(input))
    }.getOrElse {
        throw CoercingParseValueException("Expected valid ULocale but was $input")
    }

    override fun parseLiteral(input: Any): ULocale {
        val locale = (input as? StringValue)?.value
        return runCatching {
            ULocale(locale)
        }.getOrElse {
            throw CoercingParseLiteralException("Expected valid ULocale literal but was $locale")
        }
    }

    override fun serialize(dataFetcherResult: Any): String = runCatching {
        dataFetcherResult.toString()
    }.getOrElse {
        throw CoercingSerializeException("Data fetcher result $dataFetcherResult cannot be serialized to a String")
    }
}
