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

package com.expediagroup.graphql.generator.internal.extensions

import com.expediagroup.graphql.generator.annotations.GraphQLDeprecated
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.annotations.GraphQLType
import com.expediagroup.graphql.generator.annotations.GraphQLUnion
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

internal fun KAnnotatedElement.getGraphQLDescription(): String? = this.findAnnotation<GraphQLDescription>()?.value

internal fun KAnnotatedElement.getGraphQLName(): String? = this.findAnnotation<GraphQLName>()?.value

internal fun KAnnotatedElement.getDeprecationReason(): String? =
    this.findAnnotation<Deprecated>()?.getReason()
        ?: this.findAnnotation<GraphQLDeprecated>()?.getReason()

internal fun KAnnotatedElement.isGraphQLIgnored(): Boolean = this.findAnnotation<GraphQLIgnore>() != null

internal fun List<Annotation>.getUnionAnnotation(): GraphQLUnion? = this.filterIsInstance(GraphQLUnion::class.java).firstOrNull() ?: this.map { it.getMetaUnionAnnotation() }.firstOrNull()

internal fun List<Annotation>.getCustomUnionClassWithMetaUnionAnnotation(): KClass<*>? = this.firstOrNull { it.getMetaUnionAnnotation() != null }?.annotationClass

internal fun Annotation.getMetaUnionAnnotation(): GraphQLUnion? = this.annotationClass.annotations.filterIsInstance(GraphQLUnion::class.java).firstOrNull()

internal fun List<Annotation>.getCustomTypeAnnotation(): GraphQLType? = this.filterIsInstance(GraphQLType::class.java).firstOrNull()

internal fun GraphQLDeprecated.getReason(): String = when {
    replaceWith.expression.isBlank() -> message
    else -> "$message, replace with ${replaceWith.expression}"
}

internal fun Deprecated.getReason(): String = when {
    replaceWith.expression.isBlank() -> message
    else -> "$message, replace with ${replaceWith.expression}"
}
