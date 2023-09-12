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

import com.expediagroup.graphql.generator.federation.directives.Scope
import com.expediagroup.graphql.generator.federation.exception.CoercingValueToLiteralException
import graphql.GraphQLContext
import graphql.Scalars
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType
import java.util.Locale

internal const val SCOPE_SCALAR_NAME = "Scope"

/**
 * Custom scalar type that is used to represent a valid JWT scope which serializes as a String.
 */
internal val SCOPE_SCALAR_TYPE: GraphQLScalarType = GraphQLScalarType.newScalar(Scalars.GraphQLString)
    .name(SCOPE_SCALAR_NAME)
    .description("Federation type representing a JWT scope")
    .coercing(ScopeCoercing)
    .build()

private object ScopeCoercing : Coercing<Scope, String> {
    override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): String =
        when (dataFetcherResult) {
            is Scope -> dataFetcherResult.value
            else -> throw CoercingSerializeException(
                "Cannot serialize $dataFetcherResult. Expected type 'Scope' but was '${dataFetcherResult.javaClass.simpleName}'."
            )
        }

    override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): Scope =
        when (input) {
            is Scope -> input
            is StringValue -> Scope::class.constructors.first().call(input.value)
            else -> throw CoercingParseLiteralException(
                "Cannot parse $input to Scope. Expected AST type 'StringValue' but was '${input.javaClass.simpleName}'."
            )
        }

    override fun parseLiteral(input: Value<*>, variables: CoercedVariables, graphQLContext: GraphQLContext, locale: Locale): Scope =
        when (input) {
            is StringValue -> Scope::class.constructors.first().call(input.value)
            else -> throw CoercingParseLiteralException(
                "Cannot parse $input to Scope. Expected AST type 'StringValue' but was '${input.javaClass.simpleName}'."
            )
        }

    override fun valueToLiteral(input: Any, graphQLContext: GraphQLContext, locale: Locale): Value<*> =
        when (input) {
            is Scope -> StringValue.newStringValue(input.value).build()
            else -> throw CoercingValueToLiteralException(Scope::class, input)
        }
}
