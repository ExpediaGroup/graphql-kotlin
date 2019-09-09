/*
 * Copyright 2019 Expedia Group
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
import com.expediagroup.graphql.generator.extensions.getWrappedType
import graphql.schema.GraphQLList
import kotlin.reflect.KType

internal class ListBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun listType(type: KType, inputType: Boolean): GraphQLList {
        val wrappedType = graphQLTypeOf(type.getWrappedType(), inputType)
        return GraphQLList.list(wrappedType)
    }
}
