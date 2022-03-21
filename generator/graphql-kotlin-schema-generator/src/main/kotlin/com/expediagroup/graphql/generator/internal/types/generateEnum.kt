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
import com.expediagroup.graphql.generator.directives.deprecatedDirectiveWithReason
import com.expediagroup.graphql.generator.internal.extensions.getDeprecationReason
import com.expediagroup.graphql.generator.internal.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.internal.extensions.getGraphQLName
import com.expediagroup.graphql.generator.internal.extensions.getSimpleName
import com.expediagroup.graphql.generator.internal.extensions.safeCast
import com.expediagroup.graphql.generator.internal.types.utils.validateGraphQLEnumValue
import com.expediagroup.graphql.generator.internal.types.utils.validateGraphQLName
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLEnumValueDefinition
import kotlin.reflect.KClass

internal fun generateEnum(generator: SchemaGenerator, kClass: KClass<out Enum<*>>): GraphQLEnumType {
    val name = kClass.getSimpleName()
    validateGraphQLName(name, kClass)

    val enumBuilder = GraphQLEnumType.newEnum()
    enumBuilder.name(name)
    enumBuilder.description(kClass.getGraphQLDescription())

    generateDirectives(generator, kClass, DirectiveLocation.ENUM).forEach {
        enumBuilder.withAppliedDirective(it)
    }

    kClass.java.enumConstants.forEach {
        enumBuilder.value(getEnumValueDefinition(generator, it, kClass))
    }
    return generator.config.hooks.onRewireGraphQLType(enumBuilder.build(), null, generator.codeRegistry).safeCast()
}

private fun getEnumValueDefinition(generator: SchemaGenerator, enum: Enum<*>, kClass: KClass<out Enum<*>>): GraphQLEnumValueDefinition {
    val valueBuilder = GraphQLEnumValueDefinition.newEnumValueDefinition()
    val valueField = kClass.java.getField(enum.name)

    val name = valueField.getGraphQLName()
    validateGraphQLEnumValue(name, kClass)

    valueBuilder.name(name)
    valueBuilder.value(name)

    generateEnumValueDirectives(generator, valueField, kClass.getSimpleName()).forEach {
        valueBuilder.withAppliedDirective(it)
    }

    valueBuilder.description(valueField.getGraphQLDescription())

    valueField.getDeprecationReason()?.let {
        valueBuilder.deprecationReason(it)
        valueBuilder.withAppliedDirective(deprecatedDirectiveWithReason(it))
    }

    return generator.config.hooks.onRewireGraphQLType(valueBuilder.build(), null, generator.codeRegistry).safeCast()
}
