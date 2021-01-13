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

package com.expediagroup.graphql.generator.federation.execution

import com.expediagroup.graphql.generator.federation.data.BookResolver
import com.expediagroup.graphql.generator.federation.data.UserResolver
import com.expediagroup.graphql.generator.federation.data.federatedTestSchema
import com.expediagroup.graphql.generator.federation.types.ENTITIES_FIELD_NAME
import graphql.ExecutionInput
import graphql.GraphQL
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

const val FEDERATED_QUERY =
"""
query (${'$'}_representations: [_Any!]!) {
  _entities(representations: ${'$'}_representations) {
    ... on User {
      name
    }
    ... on Book {
      reviews {
        id
        body
      }
      shippingCost
    }
  }
}"""

class FederatedQueryResolverTest {

    @Test
    fun `verify can resolve federated entities`() {
        val schema = federatedTestSchema(
            federatedTypeResolvers = listOf(BookResolver(), UserResolver())
        )
        val userRepresentation = mapOf<String, Any>("__typename" to "User", "userId" to 123, "name" to "testName")
        val book1Representation = mapOf<String, Any>("__typename" to "Book", "id" to 987, "weight" to 2.0)
        val book2Representation = mapOf<String, Any>("__typename" to "Book", "id" to 988, "weight" to 1.0)
        val unknownRepresentation = mapOf<String, Any>("id" to 124)
        val representations = listOf(userRepresentation, book1Representation, book2Representation, unknownRepresentation)
        val variables = mapOf<String, Any>("_representations" to representations)
        val executionInput = ExecutionInput.newExecutionInput()
            .query(FEDERATED_QUERY)
            .variables(variables)
            .build()
        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.executeAsync(executionInput).get().toSpecification()

        assertNotNull(result["data"] as? Map<*, *>) { data ->
            assertNotNull(data[ENTITIES_FIELD_NAME] as? List<*>) { entities ->
                assertFalse(entities.isEmpty())
                assertEquals(representations.size, entities.size)
                assertNotNull(entities[0] as? Map<*, *>) { user ->
                    assertEquals("testName", user["name"])
                }
                assertNotNull(entities[1] as? Map<*, *>) { book ->
                    assertEquals("$19.98", book["shippingCost"])
                    assertNotNull(book["reviews"] as? List<*>) { reviews ->
                        assertEquals(1, reviews.size)
                        assertNotNull(reviews.firstOrNull() as? Map<*, *>) { review ->
                            assertEquals("parent-987", review["id"])
                            assertEquals("Dummy Review 987", review["body"])
                        }
                    }
                }
                assertNotNull(entities[2] as? Map<*, *>) { book ->
                    assertEquals("$9.99", book["shippingCost"])
                    assertNotNull(book["reviews"] as? List<*>) { reviews ->
                        assertEquals(1, reviews.size)
                        assertNotNull(reviews.firstOrNull() as? Map<*, *>) { review ->
                            assertEquals("parent-988", review["id"])
                            assertEquals("Dummy Review 988", review["body"])
                        }
                    }
                }
                assertNull(entities[3])
            }
        }
        assertNotNull(result["errors"] as? List<*>) { errors ->
            assertEquals(1, errors.size)
            assertNotNull(errors.firstOrNull() as? Map<*, *>) { error ->
                assertEquals("Unable to resolve federated type, representation={id=124}", error["message"])
            }
        }
    }
}
