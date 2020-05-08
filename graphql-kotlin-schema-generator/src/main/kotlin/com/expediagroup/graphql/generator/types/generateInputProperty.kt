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

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.extensions.getPropertyDescription
import com.expediagroup.graphql.generator.extensions.getPropertyName
import com.expediagroup.graphql.generator.extensions.safeCast
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal fun generateInputProperty(generator: SchemaGenerator, prop: KProperty<*>, parentClass: KClass<*>): GraphQLInputObjectField {
    val builder = GraphQLInputObjectField.newInputObjectField()
    val graphQLInputType = generateGraphQLType(generator = generator, type = prop.returnType, inputType = true).safeCast<GraphQLInputType>()

    builder.description(prop.getPropertyDescription(parentClass))
    builder.name(prop.getPropertyName(parentClass))
    builder.type(graphQLInputType)

    generateDirectives(generator, prop, parentClass).forEach {
        builder.withDirective(it)
    }

    return generator.config.hooks.onRewireGraphQLType(builder.build()).safeCast()
}
