/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.data

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeSuspendResolver
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import graphql.schema.GraphQLSchema

internal fun federatedTestSchema(
    queries: List<TopLevelObject> = emptyList(),
    federatedTypeResolvers: List<FederatedTypeSuspendResolver<*>> = emptyList(),
    isV1: Boolean = true
): GraphQLSchema {
    val config = FederatedSchemaGeneratorConfig(
        supportedPackages = if (isV1) {
            listOf("com.expediagroup.graphql.generator.federation.data.queries.federated.v1")
        } else {
            listOf("com.expediagroup.graphql.generator.federation.data.queries.federated.v2")
        },
        hooks = FederatedSchemaGeneratorHooks(federatedTypeResolvers)
    )
    return toFederatedSchema(config = config, queries = queries)
}
