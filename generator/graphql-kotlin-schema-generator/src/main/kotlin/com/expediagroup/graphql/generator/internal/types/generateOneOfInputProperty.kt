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
import com.expediagroup.graphql.generator.annotations.GraphQLOneOfFieldType
import com.expediagroup.graphql.generator.internal.extensions.safeCast
import com.expediagroup.graphql.generator.internal.types.utils.getValidOneOfUnwrappedFieldParameter
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputType
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.withNullability

internal fun generateOneOfInputProperty(
    generator: SchemaGenerator,
    metadata: OneOfFieldMetadata
): GraphQLInputObjectField {
    val graphQLInputType = generateGraphQLType(
        generator = generator,
        type = getOneOfInputFieldType(metadata.subClass, metadata.fieldType),
        typeInfo = GraphQLKTypeMetadata(inputType = true, fieldName = metadata.fieldName),
    ).safeCast<GraphQLInputType>()

    return GraphQLInputObjectField.newInputObjectField()
        .name(metadata.fieldName)
        .type(graphQLInputType)
        .build()
}

private fun getOneOfInputFieldType(
    subClass: KClass<*>,
    fieldType: GraphQLOneOfFieldType
): KType = when (fieldType) {
    GraphQLOneOfFieldType.WRAPPED -> subClass.createType(nullable = true)
    GraphQLOneOfFieldType.UNWRAPPED -> getValidOneOfUnwrappedFieldParameter(subClass).type.withNullability(true)
}
