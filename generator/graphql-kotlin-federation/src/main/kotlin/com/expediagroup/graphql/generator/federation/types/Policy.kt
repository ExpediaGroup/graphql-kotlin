/*
 * Copyright 2024 Expedia, Inc
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

import com.expediagroup.graphql.generator.federation.directives.Policy
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

internal const val POLICY_SCALAR_NAME = "Policy"

/**
 * Custom scalar type that is used to represent authentication policy which serializes as a String.
 */
internal val POLICY_SCALAR_TYPE: GraphQLScalarType = GraphQLScalarType.newScalar(Scalars.GraphQLString)
    .name(POLICY_SCALAR_NAME)
    .description("Federation type representing authorization policy")
    .coercing(PolicyCoercing)
    .build()

private object PolicyCoercing : Coercing<Policy, String> {
    override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): String =
        when (dataFetcherResult) {
            is Policy -> dataFetcherResult.value
            else -> throw CoercingSerializeException(
                "Cannot serialize $dataFetcherResult. Expected type 'Policy' but was '${dataFetcherResult.javaClass.simpleName}'."
            )
        }

    override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): Policy =
        when (input) {
            is Policy -> input
            is StringValue -> Policy::class.constructors.first().call(input.value)
            else -> throw CoercingParseLiteralException(
                "Cannot parse $input to Policy. Expected AST type 'StringValue' but was '${input.javaClass.simpleName}'."
            )
        }

    override fun parseLiteral(input: Value<*>, variables: CoercedVariables, graphQLContext: GraphQLContext, locale: Locale): Policy =
        when (input) {
            is StringValue -> Policy::class.constructors.first().call(input.value)
            else -> throw CoercingParseLiteralException(
                "Cannot parse $input to Policy. Expected AST type 'StringValue' but was '${input.javaClass.simpleName}'."
            )
        }

    override fun valueToLiteral(input: Any, graphQLContext: GraphQLContext, locale: Locale): Value<*> =
        when (input) {
            is Policy -> StringValue.newStringValue(input.value).build()
            else -> throw CoercingValueToLiteralException(Policy::class, input)
        }
}
