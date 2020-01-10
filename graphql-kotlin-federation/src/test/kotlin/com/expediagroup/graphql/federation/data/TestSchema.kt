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

package com.expediagroup.graphql.federation.data

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.federation.execution.FederatedTypeRegistry
import com.expediagroup.graphql.federation.execution.FederatedTypeResolver
import com.expediagroup.graphql.federation.toFederatedSchema
import graphql.schema.GraphQLSchema

internal fun federatedTestSchema(
    queries: List<TopLevelObject> = emptyList(),
    federatedTypeResolvers: Map<String, FederatedTypeResolver<*>> = emptyMap()
): GraphQLSchema {
    val config = FederatedSchemaGeneratorConfig(
        supportedPackages = listOf("com.expediagroup.graphql.federation.data.queries.federated"),
        hooks = FederatedSchemaGeneratorHooks(FederatedTypeRegistry(federatedTypeResolvers))
    )
    return toFederatedSchema(config = config, queries = queries)
}
