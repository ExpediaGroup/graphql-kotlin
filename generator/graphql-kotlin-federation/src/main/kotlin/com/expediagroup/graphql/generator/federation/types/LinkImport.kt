/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.types

import com.expediagroup.graphql.generator.federation.directives.LinkImport
import com.expediagroup.graphql.generator.federation.exception.CoercingValueToLiteralException
import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.ObjectField
import graphql.language.ObjectValue
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import java.util.Locale

/**
 * Custom scalar that is used to represent references to elements from imported specification.
 *
 * Imported elements can either specify the same name as in the specification OR a local custom name (i.e. rename).
 * * "@key" - simple import, using the same name as in the specification
 * * { "name": "@key", "as": "@myKey" } - imports `@key` from the specification with a local `@myKey` name
 */
internal val LINK_IMPORT_SCALAR_TYPE: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("Import")
    .coercing(LinkImportCoercing())
    .build()

private class LinkImportCoercing : Coercing<LinkImport, Any> {
    override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): Any = when (dataFetcherResult) {
        is LinkImport -> {
            if (dataFetcherResult.`as`.isBlank() || dataFetcherResult.name == dataFetcherResult.`as`) {
                dataFetcherResult.name
            } else {
                mapOf("name" to dataFetcherResult.name, "as" to dataFetcherResult.`as`)
            }
        }
        else -> throw CoercingSerializeException(
            "Cannot serialize $dataFetcherResult. Expected type `LinkImport` but was ${dataFetcherResult.javaClass.simpleName}."
        )
    }

    override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): LinkImport = when (input) {
        is LinkImport -> input
        is StringValue -> LinkImport(name = input.value, `as` = input.value)
        is ObjectValue -> {
            val nameValue = input.objectFields.firstOrNull { it.name == "name" }?.value as? StringValue ?: throw CoercingParseValueException("Cannot parse $input to LinkImport")
            val namespacedValue = input.objectFields.firstOrNull { it.name == "as" }?.value as? StringValue
            LinkImport(name = nameValue.value, `as` = namespacedValue?.value ?: nameValue.value)
        }
        else -> throw CoercingParseValueException(
            "Cannot parse $input to LinkImport. Expected AST type of `StringValue` or `ObjectValue` but was ${input.javaClass.simpleName} "
        )
    }

    override fun parseLiteral(input: Value<*>, variables: CoercedVariables, graphQLContext: GraphQLContext, locale: Locale): LinkImport =
        when (input) {
            is StringValue -> LinkImport(name = input.value, `as` = input.value)
            is ObjectValue -> {
                val nameValue = input.objectFields.firstOrNull { it.name == "name" }?.value as? StringValue ?: throw CoercingParseLiteralException("Cannot parse $input to LinkImport")
                val namespacedValue = input.objectFields.firstOrNull { it.name == "as" }?.value as? StringValue
                LinkImport(name = nameValue.value, `as` = namespacedValue?.value ?: nameValue.value)
            }
            else -> throw CoercingParseLiteralException(
                "Cannot parse $input to LinkImport. Expected AST type of `StringValue` or `ObjectValue` but was ${input.javaClass.simpleName} "
            )
        }

    override fun valueToLiteral(input: Any, graphQLContext: GraphQLContext, locale: Locale): Value<*> {
        return when (input) {
            is String -> StringValue.newStringValue(input).build()
            is LinkImport -> {
                val nameValue = StringValue.newStringValue(input.name).build()
                if (input.`as`.isBlank() || input.name == input.`as`) {
                    nameValue
                } else {
                    ObjectValue.newObjectValue()
                        .objectField(ObjectField("name", nameValue))
                        .objectField(ObjectField("as", StringValue.newStringValue(input.`as`).build()))
                        .build()
                }
            }
            else -> throw CoercingValueToLiteralException(LinkImport::class, input)
        }
    }
}
