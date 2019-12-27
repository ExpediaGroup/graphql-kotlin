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

import com.expediagroup.graphql.directives.deprecatedDirectiveWithReason
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.extensions.getDeprecationReason
import com.expediagroup.graphql.generator.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.extensions.getSimpleName
import com.expediagroup.graphql.generator.extensions.safeCast
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLEnumValueDefinition
import kotlin.reflect.KClass

internal class EnumBuilder(private val generator: SchemaGenerator) {
    internal fun enumType(kClass: KClass<out Enum<*>>): GraphQLEnumType {
        val enumBuilder = GraphQLEnumType.newEnum()

        enumBuilder.name(kClass.getSimpleName())
        enumBuilder.description(kClass.getGraphQLDescription())

        generator.directives(kClass).forEach {
            enumBuilder.withDirective(it)
        }

        kClass.java.enumConstants.forEach {
            enumBuilder.value(getEnumValueDefinition(it, kClass))
        }
        return generator.config.hooks.onRewireGraphQLType(enumBuilder.build()).safeCast()
    }

    private fun getEnumValueDefinition(enum: Enum<*>, kClass: KClass<out Enum<*>>): GraphQLEnumValueDefinition {
        val valueBuilder = GraphQLEnumValueDefinition.newEnumValueDefinition()

        valueBuilder.name(enum.name)
        valueBuilder.value(enum.name)

        val valueField = kClass.java.getField(enum.name)

        generator.fieldDirectives(valueField).forEach {
            valueBuilder.withDirective(it)
        }

        valueBuilder.description(valueField.getGraphQLDescription())

        valueField.getDeprecationReason()?.let {
            valueBuilder.deprecationReason(it)
            valueBuilder.withDirective(deprecatedDirectiveWithReason(it))
        }

        return generator.config.hooks.onRewireGraphQLType(valueBuilder.build()).safeCast()
    }
}
