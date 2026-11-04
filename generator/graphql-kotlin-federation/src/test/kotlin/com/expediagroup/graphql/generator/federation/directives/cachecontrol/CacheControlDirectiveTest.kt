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

package com.expediagroup.graphql.generator.federation.directives.cachecontrol

import com.apollographql.federation.graphqljava.caching.CacheControlInstrumentation
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.directives.CACHE_CONTROL_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.CacheControlDirective
import com.expediagroup.graphql.generator.federation.directives.CacheControlScope
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeSuspendResolver
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import graphql.ExecutionInput
import graphql.GraphQL
import graphql.schema.DataFetchingEnvironment
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLSchema
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CacheControlDirectiveTest {

    private val schema: GraphQLSchema = toFederatedSchema(
        config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.cachecontrol"),
            hooks = FederatedSchemaGeneratorHooks(listOf(ProductResolver()))
        ),
        queries = listOf(TopLevelObject(Query()))
    )

    @Test
    fun `verify cacheControl directive and CacheControlScope enum are generated`() {
        val sdl = schema.print()

        assertTrue(
            sdl.contains(
                """
                "Provides static cache hints that are used to compute the Cache-Control policy of a response"
                directive @cacheControl(inheritMaxAge: Boolean, maxAge: Int, scope: CacheControlScope) on OBJECT | FIELD_DEFINITION | INTERFACE | UNION
                """.trimIndent()
            ),
            sdl
        )
        assertTrue(
            sdl.contains(
                """
                enum CacheControlScope {
                  PRIVATE
                  PUBLIC
                }
                """.trimIndent()
            ),
            sdl
        )
        val scopeEnum = schema.getType("CacheControlScope") as? GraphQLEnumType
        assertNotNull(scopeEnum)
        assertEquals(listOf("PRIVATE", "PUBLIC"), scopeEnum.values.map { it.name }.sorted())
        // UNSET is a Kotlin-only sentinel and must never leak into the schema
        assertNull(scopeEnum.getValue("UNSET"))
    }

    @Test
    fun `verify only explicitly set arguments are applied`() {
        val expectedSchema =
            // language=GraphQL
            """
            schema @link(import : ["@key", "FieldSet"], url : "https://specs.apollo.dev/federation/v2.13"){
              query: Query
            }

            "Provides static cache hints that are used to compute the Cache-Control policy of a response"
            directive @cacheControl(inheritMaxAge: Boolean, maxAge: Int, scope: CacheControlScope) on OBJECT | FIELD_DEFINITION | INTERFACE | UNION

            "Marks the field, argument, input field or enum value as deprecated"
            directive @deprecated(
                "The reason for the deprecation"
                reason: String! = "No longer supported"
              ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION

            "This directive disables error propagation when a non nullable field returns null for the given operation."
            directive @experimental_disableErrorPropagation on QUERY | MUTATION | SUBSCRIPTION

            "Directs the executor to include this field or fragment only when the `if` argument is true"
            directive @include(
                "Included when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Space separated list of primary keys needed to access federated object"
            directive @key(fields: FieldSet!, resolvable: Boolean = true) repeatable on OBJECT | INTERFACE

            "Links definitions within the document to external schemas."
            directive @link(as: String, import: [link__Import], url: String!) repeatable on SCHEMA

            "Indicates an Input Object is a OneOf Input Object."
            directive @oneOf on INPUT_OBJECT

            "Directs the executor to skip this field or fragment when the `if` argument is true."
            directive @skip(
                "Skipped when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Exposes a URL that specifies the behaviour of this scalar."
            directive @specifiedBy(
                "The URL that specifies the behaviour of this scalar."
                url: String!
              ) on SCALAR

            interface Node @cacheControl(maxAge : 60) {
              id: String!
            }

            union SearchResult @cacheControl(maxAge : 120, scope : PRIVATE) = Product

            union _Entity = Product

            type Product implements Node @cacheControl(maxAge : 240) @key(fields : "id", resolvable : true) {
              id: String!
              name: String!
              price: Float! @cacheControl(maxAge : 30)
              related: RelatedProducts! @cacheControl(inheritMaxAge : true)
              reviews: [Review!]!
              viewerNote: String! @cacheControl(scope : PRIVATE)
            }

            type Query {
              "Union of all types that use the @key directive, including both types native to the schema and extended types"
              _entities(representations: [_Any!]!): [_Entity]!
              _service: _Service!
              node: Node!
              ping: String! @cacheControl(maxAge : 10)
              product(id: String!): Product!
              search: [SearchResult!]!
              uncached: String!
            }

            type RelatedProducts {
              ids: [String!]!
            }

            type Review {
              id: String!
            }

            type _Service {
              sdl: String!
            }

            enum CacheControlScope {
              PRIVATE
              PUBLIC
            }

            "Federation type representing set of fields"
            scalar FieldSet

            "Federation scalar type used to represent any external entities passed to _entities query."
            scalar _Any

            scalar link__Import
            """.trimIndent()
        assertEquals(expectedSchema, schema.print().trim())

        val product = assertNotNull(schema.getObjectType("Product"))
        val productHint = product.getAppliedDirective(CACHE_CONTROL_DIRECTIVE_NAME)
        assertNotNull(productHint)
        assertEquals(listOf("maxAge"), productHint.arguments.map { it.name })
        val viewerNoteHint = assertNotNull(product.getField("viewerNote").getAppliedDirective(CACHE_CONTROL_DIRECTIVE_NAME))
        assertEquals("PRIVATE", assertNotNull(viewerNoteHint.getArgument("scope")).getValue())
    }

    @Test
    fun `verify cacheControl directive is included in the _service sdl`() {
        val result = GraphQL.newGraphQL(schema).build().execute("query { _service { sdl } }")
        val data = assertNotNull(result.getData<Map<String, Any>>())
        val sdl = assertNotNull((data["_service"] as? Map<*, *>)?.get("sdl") as? String)

        assertTrue(sdl.contains("directive @cacheControl(inheritMaxAge: Boolean, maxAge: Int, scope: CacheControlScope) on OBJECT | FIELD_DEFINITION | INTERFACE | UNION"), sdl)
        assertTrue(sdl.contains("enum CacheControlScope"), sdl)
        assertTrue(sdl.contains("type Product implements Node @cacheControl(maxAge : 240) @key(fields : \"id\", resolvable : true)"), sdl)
    }

    @Test
    fun `verify federation-jvm instrumentation computes the most restrictive max age`() {
        assertEquals("max-age=30, public", cacheControlHeader("query { product(id: \"1\") { id name price } }"))
        assertEquals("max-age=240, public", cacheControlHeader("query { product(id: \"1\") { id name } }"))
    }

    @Test
    fun `verify private scope hint on a field makes the response private`() {
        assertEquals("max-age=240, private", cacheControlHeader("query { product(id: \"1\") { id viewerNote } }"))
    }

    @Test
    fun `verify field without scope hint does not reset the scope of its return type`() {
        assertEquals("max-age=120, private", cacheControlHeader("query { search { ... on Product { id } } }"))
    }

    @Test
    fun `verify composite field without hints is not cacheable`() {
        assertNull(cacheControlHeader("query { product(id: \"1\") { id reviews { id } } }"))
        assertNull(cacheControlHeader("query { uncached }"))
    }

    @Test
    fun `verify inheritMaxAge and interface and root field hints`() {
        assertEquals("max-age=240, public", cacheControlHeader("query { product(id: \"1\") { related { ids } } }"))
        assertEquals("max-age=60, public", cacheControlHeader("query { node { id } }"))
        assertEquals("max-age=10, public", cacheControlHeader("query { ping }"))
        assertEquals("max-age=10, public", cacheControlHeader("query { ping product(id: \"1\") { name } }"))
    }

    @Test
    fun `verify entity hints are used for _entities queries`() {
        val header = cacheControlHeader(
            query = "query(\$r: [_Any!]!) { _entities(representations: \$r) { ... on Product { name } } }",
            variables = mapOf("r" to listOf(mapOf("__typename" to "Product", "id" to "1")))
        )
        assertEquals("max-age=240, public", header)
    }

    private fun cacheControlHeader(query: String, variables: Map<String, Any> = emptyMap()): String? {
        val graphQL = GraphQL.newGraphQL(schema).instrumentation(CacheControlInstrumentation()).build()
        val input = ExecutionInput.newExecutionInput()
            .query(query)
            .variables(variables)
            .build()
        val result = graphQL.execute(input)
        assertTrue(result.errors.isEmpty(), result.errors.toString())
        return CacheControlInstrumentation.cacheControlHeaderFromGraphQLContext(input.graphQLContext)
    }
}

class Query {
    fun product(id: String): Product = Product(id, "product $id")

    fun node(): Node = Product("1", "product 1")

    fun search(): List<SearchResult> = listOf(Product("1", "product 1"))

    @CacheControlDirective(maxAge = 10)
    fun ping(): String = "pong"

    fun uncached(): String = "uncached"
}

@CacheControlDirective(maxAge = 60)
interface Node {
    val id: String
}

@CacheControlDirective(maxAge = 120, scope = CacheControlScope.PRIVATE)
interface SearchResult

@KeyDirective(fields = FieldSet("id"))
@CacheControlDirective(maxAge = 240)
data class Product(override val id: String, val name: String) : Node, SearchResult {
    @CacheControlDirective(maxAge = 30)
    fun price(): Double = 9.99

    @CacheControlDirective(scope = CacheControlScope.PRIVATE)
    fun viewerNote(): String = "only for you"

    fun reviews(): List<Review> = listOf(Review("r1"))

    @CacheControlDirective(inheritMaxAge = true)
    fun related(): RelatedProducts = RelatedProducts(listOf("2", "3"))
}

data class Review(val id: String)

data class RelatedProducts(val ids: List<String>)

class ProductResolver : FederatedTypeSuspendResolver<Product> {
    override val typeName: String = "Product"

    override suspend fun resolve(environment: DataFetchingEnvironment, representation: Map<String, Any>): Product =
        Product(representation["id"].toString(), "product ${representation["id"]}")
}
