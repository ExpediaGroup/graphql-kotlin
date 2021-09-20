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

import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.GraphQLScalarType

internal val graphqlULocaleType = GraphQLScalarType.newScalar()
    .name("Locale")
    .description("A type representing a Locale such as en_US or fr_FR")
    .coercing(ULocaleCoercing)
    .build()

// We coerce between <String, String> because jackson will
// take care of ser/deser for us within SchemaGenerator
private object ULocaleCoercing : Coercing<String, String> {
    override fun parseValue(input: Any): String = input as? String ?: throw CoercingParseValueException("$input can not be cast to String")

    override fun parseLiteral(input: Any): String = (input as? StringValue)?.value ?: throw CoercingParseLiteralException("$input can not be cast to StringValue")

    override fun serialize(dataFetcherResult: Any): String = dataFetcherResult.toString()
}
