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
import com.expediagroup.graphql.generator.annotations.GraphQLName
import java.lang.reflect.Field

internal fun Field.getGraphQLDescription(): String? = this.getAnnotation(GraphQLDescription::class.java)?.value

internal fun Field.getDeprecationReason(): String? =
    this.getDeclaredAnnotation(Deprecated::class.java)?.getReason()
        ?: this.getDeclaredAnnotation(GraphQLDeprecated::class.java)?.getReason()

internal fun Field.getGraphQLName(): String = this.getAnnotation(GraphQLName::class.java)?.value ?: this.name
