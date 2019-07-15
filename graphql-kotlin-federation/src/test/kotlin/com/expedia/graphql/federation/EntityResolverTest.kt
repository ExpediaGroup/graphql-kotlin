package com.expedia.graphql.federation

import graphql.ExecutionInput
import graphql.GraphQL
import io.mockk.mockk
import org.junit.jupiter.api.Test
import test.data.queries.federated.Book
import test.data.queries.federated.User
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class EntityResolverTest {

    @Test
    fun `verify can resolve federated entities`() {
        val bookResolver = object : FederatedTypeResolver<Book> {
            override fun resolve(keys: Map<String, Any>): Book {
                val book = Book(keys["id"].toString())
                keys["weight"]?.toString()?.toDoubleOrNull()?.let {
                    book.weight = it
                }
                return book
            }
        }
        val userResolver = object : FederatedTypeResolver<User> {
            override fun resolve(keys: Map<String, Any>): User {
                val id = keys["userId"].toString().toInt()
                val name = keys["name"].toString()
                return User(id, name)
            }
        }

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("test.data.queries.federated"),
            hooks = FederatedSchemaGeneratorHooks(FederatedTypeRegistry(mapOf("Book" to bookResolver, "User" to userResolver)))
        )

        val schema = toFederatedSchema(config)
        val query = """
            query (${'$'}_representations: [_Any!]!) {
              _entities(representations: ${'$'}_representations) {
                ... on User {
                  name
                }
              }
            }
        """.trimIndent()
        val representation = mapOf<String, Any>("__typename" to "User", "userId" to 123, "name" to "testName")
        val variables = mapOf<String, Any>("_representations" to listOf(representation))
        val executionInput = ExecutionInput.newExecutionInput()
            .query(query)
            .variables(variables)
            .build()
        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.executeAsync(executionInput).get()

        val data = result.toSpecification()["data"] as? Map<*, *>
        assertNotNull(data)
        val entities = data["_entities"] as? List<*>
        assertNotNull(entities)
        val user = entities.firstOrNull() as? Map<*, *>
        assertEquals("testName", user?.get("name"))
    }

    @Test
    fun `verify federated entity resolver throws exception if __typename is not specified`() {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("test.data.queries.federated"),
            hooks = FederatedSchemaGeneratorHooks(FederatedTypeRegistry(mapOf("Book" to mockk(), "User" to mockk())))
        )

        val schema = toFederatedSchema(config)
        val query = """
            query (${'$'}_representations: [_Any!]!) {
              _entities(representations: ${'$'}_representations) {
                ... on User {
                  name
                }
              }
            }
        """.trimIndent()
        val representation = emptyMap<String, Any>()
        val variables = mapOf<String, Any>("_representations" to listOf(representation))
        val executionInput = ExecutionInput.newExecutionInput()
            .query(query)
            .variables(variables)
            .build()
        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.executeAsync(executionInput).get().toSpecification()

        assertNull(result["data"])
        val errors = result["errors"] as? List<*>
        assertNotNull(errors)
        val error = errors.firstOrNull() as? Map<*, *>
        assertNotNull(error)
        assertEquals("Exception while fetching data (/_entities) : Federation exception - cannot resolve {}", error["message"])
    }
}
