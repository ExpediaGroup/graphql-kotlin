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

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.exceptions.InvalidInputFieldTypeException
import com.expediagroup.graphql.generator.internal.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.internal.extensions.getKClass
import com.expediagroup.graphql.generator.internal.extensions.getName
import com.expediagroup.graphql.generator.internal.extensions.getTypeOfFirstArgument
import com.expediagroup.graphql.generator.internal.extensions.isInterface
import com.expediagroup.graphql.generator.internal.extensions.isListType
import com.expediagroup.graphql.generator.internal.extensions.isUnion
import com.expediagroup.graphql.generator.internal.extensions.safeCast
import com.expediagroup.graphql.generator.internal.extensions.unwrapOptionalInputType
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLArgument
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType

@Throws(InvalidInputFieldTypeException::class)
internal fun generateArgument(generator: SchemaGenerator, parameter: KParameter): GraphQLArgument {

    val inputTypeFromHooks = generator.config.hooks.willResolveInputMonad(parameter.type)
    val unwrappedType = inputTypeFromHooks.unwrapOptionalInputType()

    // Validate that the input is not a polymorphic type
    // This is not currently supported by the GraphQL spec
    // https://github.com/graphql/graphql-spec/blob/master/rfcs/InputUnion.md
    val unwrappedClass = getUnwrappedClass(unwrappedType)
    if (unwrappedClass.isUnion() || unwrappedClass.isInterface()) {
        throw InvalidInputFieldTypeException(parameter)
    }

    val typeInfo = GraphQLKTypeMetadata(inputType = true, fieldName = parameter.getName(), fieldAnnotations = parameter.annotations)
    val graphQLType = generateGraphQLType(generator = generator, type = unwrappedType, typeInfo)

    // Deprecation of arguments is currently unsupported: https://youtrack.jetbrains.com/issue/KT-25643
    val builder = GraphQLArgument.newArgument()
        .name(parameter.getName())
        .description(parameter.getGraphQLDescription())
        .type(graphQLType.safeCast())

    generateDirectives(generator, parameter, DirectiveLocation.ARGUMENT_DEFINITION).forEach {
        builder.withAppliedDirective(it)
    }

    return generator.config.hooks.onRewireGraphQLType(builder.build(), null, generator.codeRegistry).safeCast()
}

private fun getUnwrappedClass(parameterType: KType): KClass<*> =
    if (parameterType.isListType()) {
        parameterType.getTypeOfFirstArgument().getKClass()
    } else {
        parameterType.getKClass()
    }
