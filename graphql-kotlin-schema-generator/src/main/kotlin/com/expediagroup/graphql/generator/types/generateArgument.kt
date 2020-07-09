/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.exceptions.InvalidInputFieldTypeException
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.extensions.getKClass
import com.expediagroup.graphql.generator.extensions.getName
import com.expediagroup.graphql.generator.extensions.getWrappedType
import com.expediagroup.graphql.generator.extensions.isInterface
import com.expediagroup.graphql.generator.extensions.isListType
import com.expediagroup.graphql.generator.extensions.isUnion
import com.expediagroup.graphql.generator.extensions.safeCast
import graphql.schema.GraphQLArgument
import kotlin.reflect.KClass
import kotlin.reflect.KParameter

@Throws(InvalidInputFieldTypeException::class)
internal fun generateArgument(generator: SchemaGenerator, parameter: KParameter): GraphQLArgument {

    // Validate that the input is not a polymorphic type
    // This is not currently supported by the GraphQL spec
    // https://github.com/graphql/graphql-spec/blob/master/rfcs/InputUnion.md
    val unwrappedClass = getUnwrappedClass(parameter)
    if (unwrappedClass.isInterface() || unwrappedClass.isUnion()) {
        throw InvalidInputFieldTypeException(parameter)
    }

    val graphQLType = generateGraphQLType(generator = generator, type = parameter.type, inputType = true)

    // Deprecation of arguments is currently unsupported: https://github.com/facebook/graphql/issues/197
    val builder = GraphQLArgument.newArgument()
        .name(parameter.getName())
        .description(parameter.getGraphQLDescription())
        .type(graphQLType.safeCast())

    generateDirectives(generator, parameter).forEach {
        builder.withDirective(it)
    }

    return generator.config.hooks.onRewireGraphQLType(builder.build()).safeCast()
}

private fun getUnwrappedClass(parameter: KParameter): KClass<*> =
    if (parameter.isListType()) {
        parameter.type.getWrappedType().getKClass()
    } else {
        parameter.type.getKClass()
    }
