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
import com.expediagroup.graphql.generator.extensions.getName
import com.expediagroup.graphql.generator.extensions.isGraphQLID
import com.expediagroup.graphql.generator.extensions.isInterface
import com.expediagroup.graphql.generator.extensions.safeCast
import graphql.schema.GraphQLArgument
import kotlin.reflect.KParameter

@Throws(InvalidInputFieldTypeException::class)
internal fun generateArgument(generator: SchemaGenerator, parameter: KParameter): GraphQLArgument {

    if (parameter.isInterface()) {
        throw InvalidInputFieldTypeException(parameter)
    }

    val graphQLType = generator.graphQLTypeOf(parameter.type, inputType = true, annotatedAsID = parameter.isGraphQLID())

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
