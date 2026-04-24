/*
 * Copyright 2024 Expedia, Inc
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
import com.expediagroup.graphql.generator.execution.convertInputMap
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
import kotlin.test.assertNull

/**
 * Verifies that plain Kotlin data classes whose fields include custom scalars are correctly
 * coerced by both:
 *
 *   1. [FunctionDataFetcher] (the resolver parameter path, via KClass.primaryConstructor), and
 *
 *   2. [getArgumentsAs] called on a [DataFetchingEnvironment] (the instrumentation path,
 *      which uses the same Kotlin reflection coercion as the resolver path).
 *
 * UUID is used as a stand-in for any custom scalar that graphql-java coerces before
 * instrumentation code accesses environment.arguments.
 */
class PlainKotlinInputWithCustomScalarTest {

    // No @GraphQLName — field names resolve to Kotlin parameter names.
    data class RequestContext(
        val requestId: UUID,
        val userId: String,
        val depth: Int = 0
    )

    data class NestedContext(
        val outer: RequestContext,
        val tag: String
    )

    class ContextQuery {
        fun processContext(context: RequestContext): String =
            "id=${context.requestId},user=${context.userId},depth=${context.depth}"

        fun processNestedContext(context: NestedContext): String =
            "tag=${context.tag},id=${context.outer.requestId},user=${context.outer.userId}"
    }

    private val schema = toSchema(
        queries = listOf(TopLevelObject(ContextQuery())),
        config = getTestSchemaConfigWithHooks(object : SchemaGeneratorHooks {
            override fun willGenerateGraphQLType(type: KType): GraphQLType? =
                when (type.classifier as? KClass<*>) {
                    UUID::class -> graphqlUUIDType
                    else -> null
                }
        })
    )

    private val graphQL = GraphQL.newGraphQL(schema).build()

    @Test
    fun `plain data class with custom scalar field resolves correctly end-to-end`() {
        val result = graphQL.execute(
            """{ processContext(context: { requestId: "550e8400-e29b-41d4-a716-446655440000", userId: "alice", depth: 0 }) }"""
        )
        assertNull(result.errors.firstOrNull(), "Expected no errors but got: ${result.errors}")
        val data: Map<String, String> = result.getData()
        assertEquals(
            "id=550e8400-e29b-41d4-a716-446655440000,user=alice,depth=0",
            data["processContext"]
        )
    }

    @Test
    fun `plain data class with nested custom scalar resolves correctly end-to-end`() {
        val result = graphQL.execute(
            """{ processNestedContext(context: { outer: { requestId: "550e8400-e29b-41d4-a716-446655440000", userId: "bob", depth: 0 }, tag: "test" }) }"""
        )
        assertNull(result.errors.firstOrNull(), "Expected no errors but got: ${result.errors}")
        val data: Map<String, String> = result.getData()
        assertEquals(
            "tag=test,id=550e8400-e29b-41d4-a716-446655440000,user=bob",
            data["processNestedContext"]
        )
    }

    @Test
    fun `coercion correctly handles pre-coerced custom scalar field`() {
        // This is the instrumentation scenario: by the time beginFieldFetch is called,
        // graphql-java has already run the scalar coercer so the UUID field in
        // environment.arguments is already a UUID object, not a string.
        val preCoercedId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        val result = convertInputMap(
            mapOf("requestId" to preCoercedId, "userId" to "alice"),
            RequestContext::class
        )

        assertEquals(preCoercedId, result.requestId)
        assertEquals("alice", result.userId)
        assertEquals(0, result.depth)
    }

    @Test
    fun `coercion correctly handles nested pre-coerced custom scalar field`() {
        val preCoercedId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
        val result = convertInputMap(
            mapOf("outer" to mapOf("requestId" to preCoercedId, "userId" to "bob"), "tag" to "instrumentation"),
            NestedContext::class
        )

        assertEquals(preCoercedId, result.outer.requestId)
        assertEquals("bob", result.outer.userId)
        assertEquals("instrumentation", result.tag)
    }
}
