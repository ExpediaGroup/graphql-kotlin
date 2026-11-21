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

package com.expediagroup.graphql.generator.semantic

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.defaultSupportedPackages
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.testSchemaConfig
import com.expediagroup.graphql.generator.toSchema
import graphql.ExecutionInput
import graphql.ExecutionResult
import graphql.GraphQL
import graphql.GraphqlErrorException
import graphql.introspection.Introspection
import graphql.introspection.IntrospectionQuery
import graphql.introspection.IntrospectionResultToSchema
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldsContainer
import graphql.schema.GraphQLSchema
import graphql.schema.idl.RuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.SchemaPrinter
import graphql.schema.visibility.BlockedFields
import graphql.schema.visibility.GraphqlFieldVisibility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.concurrent.CancellationException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("Detekt.UseDataClass", "Detekt.FunctionOnlyReturningConstant", "Detekt.UnusedPrivateMember")
class SemanticIntrospectionTest {

    class Query {
        fun repository(name: String) = Repository(name)
        fun orders(filter: OrderFilter?): List<OrderStatus> = emptyList()
    }

    class OrderFilter(val status: OrderStatus)

    enum class OrderStatus { PENDING }

    class Repository(val name: String) {
        fun releases(): List<Release> = emptyList()
    }

    class Release(val tagName: String)

    private fun schema(config: SchemaGeneratorConfig = testSchemaConfig()): GraphQLSchema =
        toSchema(queries = listOf(TopLevelObject(Query())), config = config)

    private val releases = SemanticSearchResult("Repository.releases", listOf(listOf("Query.repository", "Repository.releases")), 0.9, "c0")

    private fun execute(schema: GraphQLSchema, query: String, context: Map<Any, Any> = emptyMap()): ExecutionResult =
        GraphQL.newGraphQL(schema).build().execute(ExecutionInput.newExecutionInput(query).graphQLContext(context))

    private fun ExecutionResult.search(): List<Map<String, Any?>>? = getData<Map<String, List<Map<String, Any?>>?>>()?.get("_search")

    @Test
    fun `adds _search and _SearchResult, keeping references to Query`() {
        val schema = schema().withSemanticIntrospection { _, _ -> emptyList() }

        val search = schema.queryType.getFieldDefinition(SEMANTIC_SEARCH_FIELD)
        assertEquals(listOf("query", "first", "after", "minScore"), search.arguments.map { it.name })
        assertEquals(listOf("coordinate", "pathsToRoot", "score", "cursor"), schema.getObjectType(SEMANTIC_SEARCH_RESULT_TYPE)!!.fieldDefinitions.map { it.name })
    }

    @Test
    fun `keeps the schema's SDL definitions`() {
        val sdl = "directive @tag on SCHEMA schema { query: Root } type Root { name: String } extend schema @tag"
        val parsed = SchemaGenerator().makeExecutableSchema(SchemaParser().parse(sdl), RuntimeWiring.MOCKED_WIRING)
        val schema = parsed.withSemanticIntrospection { _, _ -> emptyList() }
        assertNotNull(schema.definition)
        assertEquals(1, schema.extensionDefinitions.size)
        assertNotNull(schema.queryType.getFieldDefinition(SEMANTIC_SEARCH_FIELD))
    }

    @Test
    fun `types referencing Query see the transformed Query`() {
        val sdl = "type Query { self: Query name: String }"
        val schema = SchemaGenerator().makeExecutableSchema(SchemaParser().parse(sdl), RuntimeWiring.MOCKED_WIRING)
            .withSemanticIntrospection { _, _ -> emptyList() }
        assertTrue(execute(schema, """{ self { _search(query: "x") { coordinate } } }""").errors.isEmpty())
    }

    @Test
    fun `resolves _search with the request arguments`() {
        var request: SemanticSearchRequest? = null
        val schema = schema().withSemanticIntrospection { _, r -> request = r; listOf(releases) }

        val result = execute(schema, """{ _search(query: "releases", first: 2, after: "c", minScore: 0.5) { coordinate pathsToRoot score cursor } }""")

        assertEquals(emptyList(), result.errors)
        assertEquals(
            listOf(mapOf("coordinate" to "Repository.releases", "pathsToRoot" to listOf(listOf("Query.repository", "Repository.releases")), "score" to 0.9, "cursor" to "c0")),
            result.search()
        )
        assertEquals(listOf("releases", 2, "c", 0.5), request!!.let { listOf(it.query, it.first, it.after, it.minScore) })
    }

    @Test
    fun `defaults first to 10 and rejects less than 1`() {
        var first = 0
        val schema = schema().withSemanticIntrospection { _, r -> first = r.first; emptyList() }
        execute(schema, """{ _search(query: "x") { coordinate } }""")
        assertEquals(10, first)

        val result = execute(schema, """{ _search(query: "x", first: 0) { coordinate } }""")
        assertNull(result.search())
        assertEquals(listOf("first must be at least 1"), result.errors.map { it.message })
    }

    @Test
    fun `invalid pages fail the field instead of being trimmed`() {
        fun errorsFor(results: List<SemanticSearchResult>, query: String = """{ _search(query: "x", first: 1, minScore: 0.5) { coordinate } }""") =
            execute(schema().withSemanticIntrospection { _, _ -> results }, query).also { assertNull(it.search()) }.errors

        assertEquals(1, errorsFor(listOf(releases, releases)).size)
        assertEquals(1, errorsFor(listOf(SemanticSearchResult("Repository.releases", emptyList(), 0.1, "c"))).size)
        assertEquals(1, errorsFor(listOf(SemanticSearchResult("Repository.releases", emptyList(), null, "c"))).size)
        val invalid = listOf(
            "Repository.nope", "Nope", "A.B.C", "String.length", "Query._search", "_SearchResult", "_SearchResult.cursor", "__Type", "__Type.name",
            "__TypeKind.OBJECT", "", "Query.repository(name:\t)", "Query.repository(nope:)", "Repository(name:)", "@nope", "@deprecated(nope:)", "@deprecated.reason", "OrderStatus.DONE"
        )
        for (coordinate in invalid) {
            val errors = errorsFor(listOf(SemanticSearchResult(coordinate, emptyList(), 0.9, "c")))
            assertEquals(1, errors.size, coordinate)
            // never echoes the coordinate, which may be hidden
            assertFalse(coordinate.isNotEmpty() && coordinate in errors.single().message, coordinate)
        }
    }

    @Test
    fun `accepts every RFC coordinate form for visible members`() {
        val valid = listOf(
            "Repository", "Repository.releases", "Query.repository(name:)", "Query.repository(name: )", "OrderFilterInput.status", "OrderStatus.PENDING",
            "@deprecated", "@deprecated(reason:)"
        )
        for (coordinate in valid) {
            val schema = schema().withSemanticIntrospection { _, _ -> listOf(SemanticSearchResult(coordinate, emptyList(), 0.9, "c")) }
            assertEquals(listOf(mapOf("coordinate" to coordinate)), execute(schema, """{ _search(query: "x") { coordinate } }""").search(), coordinate)
        }
    }

    @Test
    fun `accepts type coordinates and drops paths through unknown fields`() {
        val schema = schema().withSemanticIntrospection { _, _ ->
            listOf(SemanticSearchResult("Repository", listOf(listOf("Query.repository"), listOf("Query.hidden", "Repository.releases"), emptyList()), 0.9, "c"))
        }
        val result = execute(schema, """{ _search(query: "x") { coordinate pathsToRoot } }""")
        assertEquals(listOf(mapOf("coordinate" to "Repository", "pathsToRoot" to listOf(listOf("Query.repository")))), result.search())
    }

    // Hides fields matching [hidden], delegating everything else, like application decorators do.
    private class Hiding(private val delegate: GraphqlFieldVisibility, private val hidden: (String) -> Boolean) : GraphqlFieldVisibility {
        override fun getFieldDefinitions(container: GraphQLFieldsContainer): List<GraphQLFieldDefinition> =
            delegate.getFieldDefinitions(container).filterNot { hidden(it.name) }
        override fun getFieldDefinition(container: GraphQLFieldsContainer, name: String): GraphQLFieldDefinition? =
            delegate.getFieldDefinition(container, name)?.takeUnless { hidden(name) }
    }

    private fun GraphQLSchema.withVisibility(visibility: (GraphqlFieldVisibility) -> GraphqlFieldVisibility) =
        GraphQLSchema.newSchema(this).codeRegistry(codeRegistry.transform { it.fieldVisibility(visibility(codeRegistry.fieldVisibility)) }).build()

    @Test
    fun `hidden fields are not valid coordinates`() {
        val schema = schema().withVisibility { Hiding(it) { name -> name == "releases" } }.withSemanticIntrospection { _, _ -> listOf(releases) }
        assertNull(execute(schema, """{ _search(query: "x") { coordinate } }""").search())
    }

    @Test
    fun `resolver errors reach the client as GraphQL errors`() {
        val clientError = schema().withSemanticIntrospection { _, _ -> throw GraphqlErrorException.newErrorException().message("query too long").build() }
        assertEquals(listOf("query too long"), execute(clientError, """{ _search(query: "x") { coordinate } }""").errors.map { it.message })

        // converted inside the coroutine, so the request's scope survives
        val job = Job()
        execute(clientError, """{ _search(query: "x") { coordinate } }""", mapOf(CoroutineScope::class to CoroutineScope(job)))
        assertTrue(job.isActive)

        val failure = schema().withSemanticIntrospection { _, _ -> error("provider down") }
        val result = execute(failure, """{ _search(query: "x") { coordinate } }""")
        assertNull(result.search())
        assertEquals(1, result.errors.size)
    }

    @Test
    fun `runs in the request's coroutine scope`() {
        val job = Job().apply { cancel(CancellationException("client went away")) }
        val schema = schema().withSemanticIntrospection { _, _ -> error("not reached") }
        val result = execute(schema, """{ _search(query: "x") { coordinate } }""", mapOf(CoroutineScope::class to CoroutineScope(job)))
        assertNull(result.search())
    }

    @Test
    fun `respects introspection being disabled`() {
        val schema = schema().withSemanticIntrospection { _, _ -> listOf(releases) }
        val perRequest = execute(schema, """{ _search(query: "x") { coordinate } }""", mapOf(Introspection.INTROSPECTION_DISABLED to true))
        assertNull(perRequest.search())
        assertEquals(1, perRequest.errors.size)

        // hidden by the executing schema's visibility after _search was added
        val hidden = schema.withVisibility { BlockedFields.newBlock().addPattern("__.*").build() }
        assertNull(execute(hidden, """{ _search(query: "x") { coordinate } }""").search())

        Introspection.enabledJvmWide(false)
        try {
            assertNull(execute(schema, """{ _search(query: "x") { coordinate } }""").search())
        } finally {
            Introspection.enabledJvmWide(true)
        }

        val noIntrospection = schema(SchemaGeneratorConfig(defaultSupportedPackages, introspectionEnabled = false))
        assertThrows<IllegalArgumentException> { noIntrospection.withSemanticIntrospection { _, _ -> emptyList() } }
        // also through a decorating visibility
        assertThrows<IllegalArgumentException> {
            noIntrospection.withVisibility { Hiding(it) { false } }.withSemanticIntrospection { _, _ -> emptyList() }
        }
    }

    @Test
    fun `refuses to replace existing names`() {
        val schema = schema().withSemanticIntrospection { _, _ -> emptyList() }
        assertThrows<IllegalStateException> { schema.withSemanticIntrospection { _, _ -> emptyList() } }
        val ownType = SchemaGenerator().makeExecutableSchema(SchemaParser().parse("type Query { a: _SearchResult } type _SearchResult { b: String }"), RuntimeWiring.MOCKED_WIRING)
        assertThrows<IllegalStateException> { ownType.withSemanticIntrospection { _, _ -> emptyList() } }
    }

    @Test
    fun `print leaves _search out unless introspection types are printed, and introspection round-trips`() {
        val plain = schema().print()
        val schema = schema().withSemanticIntrospection { _, _ -> emptyList() }

        assertEquals(plain, schema.print())
        assertTrue("_search" in schema.print(includeIntrospectionTypes = true))

        val introspection = execute(schema, IntrospectionQuery.INTROSPECTION_QUERY).getData<Map<String, Any>>()
        val sdl = SchemaPrinter().print(IntrospectionResultToSchema().createSchemaDefinition(introspection))
        assertTrue("_search(" in sdl && "type _SearchResult" in sdl, sdl)
    }

    class OwnSearch {
        @Suppress("Detekt.FunctionNaming")
        fun _search(): String = "mine"
    }

    @Test
    fun `print keeps an application's own _search`() {
        val schema = toSchema(queries = listOf(TopLevelObject(OwnSearch())), config = testSchemaConfig())
        assertTrue("_search: String!" in schema.print())
        assertFalse(schema.print().contains(SEMANTIC_SEARCH_RESULT_TYPE))
    }
}
