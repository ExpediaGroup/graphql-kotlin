/*
 * Copyright 2022 Expedia, Inc
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

import com.apollographql.federation.graphqljava._FieldSet
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.exception.CoercingValueToLiteralException
import graphql.Scalars
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingSerializeException
import graphql.schema.GraphQLScalarType

/**
 * Custom scalar type that is used to represent a set of fields. Grammatically, a field set is a selection set minus the braces.
 * This means it can represent a single field "upc", multiple fields "id countryCode", and even nested selection sets
 * "id organization { id }".
 */
internal val FIELD_SET_SCALAR_TYPE: GraphQLScalarType = GraphQLScalarType.newScalar(Scalars.GraphQLString)
    .name("_FieldSet")
    .description("Federation type representing set of fields")
    .coercing(FieldSetCoercing)
    .build()

private object FieldSetCoercing : Coercing<FieldSet, String> {
    override fun serialize(input: Any): String = if (input is FieldSet) {
        input.value
    } else {
        throw CoercingSerializeException("Cannot serialize $input. Expected type 'FieldSet' but was '${input.javaClass.simpleName}'.")
    }

    override fun parseValue(input: Any): FieldSet = parseLiteral(input)

    override fun parseLiteral(input: Any): FieldSet =
        when (input) {
            is FieldSet -> input
            is StringValue -> FieldSet::class.constructors.first().call(input.value)
            else -> {
                throw CoercingParseLiteralException("Cannot parse $input to FieldSet. Expected AST type 'StringValue' but was '${input.javaClass.simpleName}'.")
            }
        }

    override fun valueToLiteral(input: Any): Value<out Value<*>> = if (input is FieldSet) {
        StringValue.newStringValue(input.value).build()
    } else {
        throw CoercingValueToLiteralException(_FieldSet::class, input)
    }
}
