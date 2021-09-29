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
import java.util.UUID

internal val graphqlUUIDType = GraphQLScalarType.newScalar()
    .name("UUID")
    .description("Custom scalar representing UUID")
    .coercing(object : Coercing<UUID, String> {
        override fun parseValue(input: Any): UUID = try {
            UUID.fromString(
                serialize(input)
            )
        } catch (e: Exception) {
            throw CoercingParseValueException("Cannot parse value $input to UUID", e)
        }

        override fun parseLiteral(input: Any): UUID = try {
            val uuidString = (input as? StringValue)?.value
            UUID.fromString(uuidString)
        } catch (e: Exception) {
            throw CoercingParseLiteralException("Cannot parse literal $input to UUID", e)
        }

        override fun serialize(dataFetcherResult: Any): String = dataFetcherResult.toString()
    })
    .build()
