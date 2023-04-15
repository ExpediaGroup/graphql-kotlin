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

package com.expediagroup.graphql.plugin.test.hooks

import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import java.util.Locale
import java.util.UUID
import kotlin.reflect.KType

private val graphqlUUIDType = GraphQLScalarType.newScalar()
    .name("UUID")
    .description("Custom scalar representing UUID")
    .coercing(object : Coercing<UUID, String> {

        override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): UUID =
            parseValue(input)

        override fun parseLiteral(input: Value<*>, variables: CoercedVariables, graphQLContext: GraphQLContext, locale: Locale): UUID =
            parseLiteral(input)

        override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): String =
            serialize(dataFetcherResult)

        override fun parseValue(input: Any): UUID = try {
            UUID.fromString(serialize(input))
        } catch (e: Exception) {
            throw CoercingParseValueException("Unable to convert value $input to UUID")
        }

        override fun parseLiteral(input: Any): UUID =
            (input as? StringValue)?.value?.let(UUID::fromString) ?: throw CoercingParseLiteralException("Unable to convert value $input to UUID")

        override fun serialize(dataFetcherResult: Any): String = dataFetcherResult.toString()
    })
    .build()

class CustomFederatedHooks : FederatedSchemaGeneratorHooks(emptyList()) {

    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
        UUID::class -> graphqlUUIDType
        else -> super.willGenerateGraphQLType(type)
    }
}
