/*
 * Copyright 2026 Expedia, Inc
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
import com.expediagroup.graphql.generator.annotations.GraphQLOneOfField
import com.expediagroup.graphql.generator.annotations.GraphQLOneOfFieldType
import com.expediagroup.graphql.generator.annotations.GraphQLValidObjectLocations
import com.expediagroup.graphql.generator.exceptions.MissingOneOfInputFieldAnnotationException
import com.expediagroup.graphql.generator.internal.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.internal.extensions.getSimpleName
import com.expediagroup.graphql.generator.internal.extensions.safeCast
import com.expediagroup.graphql.generator.internal.types.utils.validateGraphQLName
import com.expediagroup.graphql.generator.internal.types.utils.validateObjectLocation
import com.expediagroup.graphql.generator.internal.types.utils.validateOneOfInputDuplicatedFieldNames
import com.expediagroup.graphql.generator.internal.types.utils.validateOneOfInputObjectSealedInterface
import graphql.Directives
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLInputObjectType
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

internal fun generateOneOfInputObject(generator: SchemaGenerator, kClass: KClass<*>): GraphQLInputObjectType {
    validateObjectLocation(kClass, GraphQLValidObjectLocations.Locations.INPUT_OBJECT)
    validateOneOfInputObjectSealedInterface(kClass)

    val name = kClass.getSimpleName(isInputClass = true)
    validateGraphQLName(name, kClass)

    val builder = GraphQLInputObjectType.newInputObject().apply {
        name(name)
        description(kClass.getGraphQLDescription())
        withAppliedDirective(Directives.OneOfDirective.toAppliedDirective())
        generateDirectives(generator, kClass, DirectiveLocation.INPUT_OBJECT).forEach {
            withAppliedDirective(it)
        }
    }

    val fields = kClass.sealedSubclasses.map { subClass ->
        val graphQLOneOfFieldAnnotation = subClass.findAnnotation<GraphQLOneOfField>()
            ?: throw MissingOneOfInputFieldAnnotationException(kClass, subClass)

        validateGraphQLName(graphQLOneOfFieldAnnotation.fieldName, subClass)

        OneOfFieldMetadata(
            graphQLOneOfFieldAnnotation.fieldName,
            graphQLOneOfFieldAnnotation.type,
            subClass
        )
    }

    validateOneOfInputDuplicatedFieldNames(kClass, fields.map(OneOfFieldMetadata::fieldName))

    fields.forEach { metadata ->
        val field = generateOneOfInputProperty(generator, metadata)
        builder.field(field)
    }

    return generator.config.hooks.onRewireGraphQLType(builder.build(), null, generator.codeRegistry).safeCast()
}

internal data class OneOfFieldMetadata(
    val fieldName: String,
    val fieldType: GraphQLOneOfFieldType,
    val subClass: KClass<*>,
)
