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

package com.expediagroup.graphql.federation.types

import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLUnionType

/**
 * Generates union of all types that use the  @key directive, including both types native to the schema and extended types.
 */
@Suppress("SpreadOperator")
internal fun generateEntityFieldDefinition(federatedTypes: Set<String>): GraphQLFieldDefinition {
    val possibleTypes = federatedTypes
        .map { GraphQLTypeReference(it) }
        .toTypedArray()

    return GraphQLFieldDefinition.newFieldDefinition()
        .name("_entities")
        .description("Union of all types that use the @key directive, including both types native to the schema and extended types")
        .argument(GraphQLArgument.newArgument()
            .name("representations")
            .type(GraphQLNonNull(
                GraphQLList(
                    GraphQLNonNull(
                        GraphQLTypeReference(ANY_SCALAR_TYPE.name)
                    )
                )
            ))
            .build())
        .type(GraphQLNonNull(
            GraphQLList(
                GraphQLUnionType.newUnionType()
                    .name("_Entity")
                    .possibleTypes(*possibleTypes)
                    .build()
            )
        ))
        .build()
}
