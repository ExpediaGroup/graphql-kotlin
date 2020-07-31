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

package com.expediagroup.graphql.generator.extensions

import com.expediagroup.graphql.exceptions.CouldNotCastGraphQLSchemaElement
import com.expediagroup.graphql.extensions.deepName
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLSchemaElement
import graphql.schema.GraphQLType
import kotlin.reflect.KType

/**
 * Map null and non-null types.
 * Returns same type when wrapping a non-null graphql type twice.
 */
internal fun GraphQLType.wrapInNonNull(type: KType): GraphQLType = when {
    this is GraphQLNonNull -> this
    type.isMarkedNullable -> this
    else -> GraphQLNonNull.nonNull(this)
}

/**
 * Get a distinct name for the class so we can tell input and object types
 * apart and create a set of truly unique types.
 */
internal fun GraphQLType.distinctName(): String = "${this.javaClass.name}.${this.deepName}"

@Throws(CouldNotCastGraphQLSchemaElement::class)
internal inline fun <reified T : GraphQLSchemaElement> GraphQLSchemaElement.safeCast(): T {
    if (this !is T) throw CouldNotCastGraphQLSchemaElement(this, T::class)
    return this
}

internal fun GraphQLDirectiveContainer.getAllDirectives(): List<GraphQLDirective> {
    // A function without directives may still be rewired if the arguments have directives
    // see https://github.com/ExpediaGroup/graphql-kotlin/wiki/Schema-Directives for details
    val mutableList = mutableListOf<GraphQLDirective>()

    mutableList.addAll(this.directives)

    if (this is GraphQLFieldDefinition) {
        this.arguments.forEach { mutableList.addAll(it.directives) }
    }

    return mutableList
}
