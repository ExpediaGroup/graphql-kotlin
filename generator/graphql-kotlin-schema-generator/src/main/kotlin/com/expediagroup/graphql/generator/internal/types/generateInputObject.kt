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
import com.expediagroup.graphql.generator.annotations.GraphQLValidObjectLocations
import com.expediagroup.graphql.generator.internal.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.internal.extensions.getSimpleName
import com.expediagroup.graphql.generator.internal.extensions.getValidProperties
import com.expediagroup.graphql.generator.internal.extensions.safeCast
import com.expediagroup.graphql.generator.internal.types.utils.validateGraphQLName
import com.expediagroup.graphql.generator.internal.types.utils.validateObjectLocation
import com.expediagroup.graphql.generator.internal.types.utils.validatePrimaryConstructorExists
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLInputObjectType
import kotlin.reflect.KClass

internal fun generateInputObject(generator: SchemaGenerator, kClass: KClass<*>): GraphQLInputObjectType {
    validateObjectLocation(kClass, GraphQLValidObjectLocations.Locations.INPUT_OBJECT)
    validatePrimaryConstructorExists(kClass)

    val name = kClass.getSimpleName(isInputClass = true)
    validateGraphQLName(name, kClass)

    val builder = GraphQLInputObjectType.newInputObject()
    builder.name(name)
    builder.description(kClass.getGraphQLDescription())

    generateDirectives(generator, kClass, DirectiveLocation.INPUT_OBJECT).forEach {
        builder.withAppliedDirective(it)
    }

    // It does not make sense to run functions against the input types so we only process the properties
    kClass.getValidProperties(generator.config.hooks).forEach {
        builder.field(generateInputProperty(generator, it, kClass))
    }

    return generator.config.hooks.onRewireGraphQLType(builder.build(), null, generator.codeRegistry).safeCast()
}
