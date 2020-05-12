/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.federation.extensions

import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLSchema

/**
 * Add all the directives to the schema if they are not present.
 * Returns a new schema builder so you can continue adding more types if needed.
 */
internal fun GraphQLSchema.addDirectivesIfNotPresent(directives: List<GraphQLDirective>): GraphQLSchema.Builder {
    val newBuilder = GraphQLSchema.newSchema(this)

    directives.forEach {
        if (this.getDirective(it.name) == null) {
            newBuilder.additionalDirective(it)
        }
    }

    return newBuilder
}
