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
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.exceptions.InvalidSchemaTypeException
import com.expediagroup.graphql.generator.internal.extensions.getValidFunctions
import com.expediagroup.graphql.generator.internal.extensions.getValidProperties
import graphql.introspection.Introspection
import graphql.schema.GraphQLAppliedDirective

internal fun generateSchemaDirectives(generator: SchemaGenerator, schema: TopLevelObject? = null): List<GraphQLAppliedDirective> = if (schema != null) {
    if (schema.kClass.getValidProperties(generator.config.hooks).isNotEmpty() || schema.kClass.getValidFunctions(generator.config.hooks).isNotEmpty()) {
        throw InvalidSchemaTypeException(schema.kClass)
    }
    generateDirectives(generator, schema.kClass, Introspection.DirectiveLocation.SCHEMA)
} else {
    emptyList()
}
