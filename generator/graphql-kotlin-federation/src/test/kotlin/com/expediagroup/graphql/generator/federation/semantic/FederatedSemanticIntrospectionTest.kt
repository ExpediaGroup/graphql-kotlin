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

@file:OptIn(ExperimentalSemanticIntrospection::class)

package com.expediagroup.graphql.generator.federation.semantic

import com.expediagroup.graphql.generator.federation.data.BookResolver
import com.expediagroup.graphql.generator.federation.data.UserResolver
import com.expediagroup.graphql.generator.federation.data.federatedTestSchema
import com.expediagroup.graphql.generator.federation.execution.FEDERATED_QUERY
import com.expediagroup.graphql.generator.semantic.ExperimentalSemanticIntrospection
import com.expediagroup.graphql.generator.semantic.withSemanticIntrospection
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.schema.GraphQLSchema
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FederatedSemanticIntrospectionTest {

    private fun execute(schema: GraphQLSchema, query: String, variables: Map<String, Any> = emptyMap()) =
        GraphQL.newGraphQL(schema).build().execute(ExecutionInput.newExecutionInput(query).variables(variables))

    @Test
    fun `_service SDL is unchanged and _entities still resolves`() {
        val federated = federatedTestSchema(federatedTypeResolvers = listOf(BookResolver(), UserResolver()))
        val schema = federated.withSemanticIntrospection { _, _ -> emptyList() }

        val sdl = { s: GraphQLSchema -> execute(s, "{ _service { sdl } }").getData<Map<String, Map<String, String>>>()!!["_service"]!!["sdl"]!! }
        assertEquals(sdl(federated), sdl(schema))
        assertTrue("_search" !in sdl(schema))

        val representations = listOf(mapOf("__typename" to "User", "userId" to 123, "name" to "testName"))
        val entities = execute(schema, FEDERATED_QUERY, mapOf("_representations" to representations))
        assertEquals(emptyList(), entities.errors)
        assertEquals(mapOf("_entities" to listOf(mapOf("name" to "testName"))), entities.getData())
    }
}
