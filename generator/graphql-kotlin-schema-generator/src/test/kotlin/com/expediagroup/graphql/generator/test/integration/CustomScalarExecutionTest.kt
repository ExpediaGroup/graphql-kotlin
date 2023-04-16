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

package com.expediagroup.graphql.generator.test.integration

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.getTestSchemaConfigWithHooks
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.generator.test.utils.graphqlUUIDType
import com.expediagroup.graphql.generator.toSchema
import graphql.GraphQL
import graphql.schema.GraphQLType
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull

class CustomScalarExecutionTest {

    private val schema = toSchema(
        queries = listOf(TopLevelObject(QueryObject())),
        config = getTestSchemaConfigWithHooks(CustomScalarHooks())
    )
    private val graphQL: GraphQL = GraphQL.newGraphQL(schema).build()

    @Test
    fun `a custom scalar can be used as output`() {
        val result = graphQL.execute("{ uuidOutput }")
        val data: Map<String, String>? = result.getData()

        assertNotNull(data?.get("uuidOutput"))
    }

    @Test
    fun `a custom scalar can be used as input`() {
        val result = graphQL.execute("{ uuidInput(uuid: \"22dba784-f7c1-471f-8e8c-84e3a59bb5b5\") }")
        val data: Map<String, String>? = result.getData()

        assertNotNull(data?.get("uuidInput"))
    }

    @Test
    fun `an invalid custom scalar as input will throw an error`() {
        assertFails {
            graphQL.execute("{ uuidInput(uuid: \"foo\") }")
        }
    }

    @Test
    fun `a custom scalar can be used as input in a list`() {
        val result = graphQL.execute("{ uuidListInput(uuids: [\"435f5808-936b-40ab-89b6-ec4e8cd1ba36\", \"435f5808-936b-40ab-89b6-ec4e8cd1ba36\"]) }")
        val data: Map<String, String>? = result.getData()

        assertEquals("You sent 2 items and there are 1 unqiue items", data?.get("uuidListInput"))
    }

    class QueryObject {
        fun uuidOutput(): UUID = UUID.randomUUID()

        fun uuidInput(uuid: UUID) = "You sent $uuid"

        fun uuidListInput(uuids: List<UUID>): String {
            // This verifies that the custom scalar is converted properly by jackson
            // and we can run comparisons on the original class
            val group = uuids.groupBy { it }
            return "You sent ${uuids.size} items and there are ${group.size} unqiue items"
        }
    }

    class CustomScalarHooks : SchemaGeneratorHooks {
        override fun willGenerateGraphQLType(type: KType): GraphQLType? {
            return when (type.classifier as? KClass<*>) {
                UUID::class -> graphqlUUIDType
                else -> null
            }
        }
    }
}
