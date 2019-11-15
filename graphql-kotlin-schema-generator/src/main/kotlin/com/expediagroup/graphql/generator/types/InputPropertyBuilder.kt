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
import com.expediagroup.graphql.generator.TypeBuilder
import com.expediagroup.graphql.generator.extensions.getPropertyDescription
import com.expediagroup.graphql.generator.extensions.getPropertyName
import com.expediagroup.graphql.generator.extensions.getPropertyNullable
import com.expediagroup.graphql.generator.extensions.isPropertyGraphQLID
import com.expediagroup.graphql.generator.extensions.safeCast
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal class InputPropertyBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    internal fun inputProperty(prop: KProperty<*>, parentClass: KClass<*>): GraphQLInputObjectField {
        val builder = GraphQLInputObjectField.newInputObjectField()

        builder.description(prop.getPropertyDescription(parentClass))
        builder.name(prop.getPropertyName(parentClass))
        builder.type(graphQLTypeOf(prop.returnType, true, prop.isPropertyGraphQLID(parentClass), prop.getPropertyNullable(parentClass)).safeCast<GraphQLInputType>())

        generator.directives(prop, parentClass).forEach {
            builder.withDirective(it)
        }

        return config.hooks.onRewireGraphQLType(builder.build()).safeCast()
    }
}
