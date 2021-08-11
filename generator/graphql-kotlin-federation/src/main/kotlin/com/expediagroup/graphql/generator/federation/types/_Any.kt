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

package com.expediagroup.graphql.generator.federation.types

import graphql.language.ArrayValue
import graphql.language.BooleanValue
import graphql.language.EnumValue
import graphql.language.FloatValue
import graphql.language.IntValue
import graphql.language.ObjectValue
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.GraphQLScalarType

/**
 * The _Any scalar is used to pass representations of entities from external services into the root _entities field for execution.
 * Validation of the _Any scalar is done by matching the __typename and @external fields defined in the schema.
 */
internal val ANY_SCALAR_TYPE: GraphQLScalarType = GraphQLScalarType.newScalar()
    .name("_Any")
    .description("Federation scalar type used to represent any external entities passed to _entities query.")
    .coercing(AnyCoercing)
    .build()

private object AnyCoercing : Coercing<Any, Any> {

    override fun serialize(dataFetcherResult: Any): Any = dataFetcherResult

    override fun parseValue(input: Any): Any = input

    @Suppress("ComplexMethod")
    override fun parseLiteral(input: Any): Any =
        when (input) {
            is FloatValue -> input.value
            is StringValue -> input.value
            is IntValue -> input.value
            is BooleanValue -> input.isValue
            is EnumValue -> input.name
            is ArrayValue -> input.values.map { parseLiteral(it) }
            is ObjectValue -> input.objectFields.associateBy({ it.name }, { parseLiteral(it.value) })
            else -> throw CoercingParseLiteralException("Cannot parse $input to Any scalar")
        }
}
