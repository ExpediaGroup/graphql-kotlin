package com.expedia.graphql.federation.execution

import graphql.GraphQLError
import graphql.schema.DataFetchingEnvironment
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Test
import test.data.BookResolver
import test.data.UserResolver
import test.data.queries.federated.Book
import test.data.queries.federated.User
import kotlin.test.assertEquals

class EntityQueryResolverTest {

    @Test
    fun `verify can resolve federated entities`() {
        val resolver = EntityResolver(FederatedTypeRegistry(mapOf("User" to UserResolver())))
        val representations = listOf(mapOf<String, Any>("__typename" to "User", "userId" to 123, "name" to "testName"))
        val env = mockk<DataFetchingEnvironment> {
            every { getArgument<Any>(any()) } returns representations
        }

        val result = resolver.get(env).get()
        verifyData(result.data, User(123, "testName"))
        verifyErrors(result.errors)
    }

    @Test
    fun `verify federated entity resolver returns GraphQLError if __typename is not specified`() {
        val resolver = EntityResolver(FederatedTypeRegistry(mapOf("Book" to mockk(), "User" to mockk())))
        val env = mockk<DataFetchingEnvironment> {
            every { getArgument<Any>(any()) } returns listOf(emptyMap<String, Any>())
        }

        val result = resolver.get(env).get()
        verifyData(result.data, null)
        verifyErrors(result.errors, "Unable to resolve federated type, representation={}")
    }

    @Test
    fun `verify federated entity resolver returns GraphQLError if __typename cannot be resolved`() {
        val resolver = EntityResolver(FederatedTypeRegistry())
        val representations = listOf(mapOf<String, Any>("__typename" to "User", "userId" to 123, "name" to "testName"))
        val env = mockk<DataFetchingEnvironment> {
            every { getArgument<Any>(any()) } returns representations
        }

        val result = resolver.get(env).get()
        verifyData(result.data, null)
        verifyErrors(result.errors, "Unable to resolve federated type, representation={__typename=User, userId=123, name=testName}")
    }

    @Test
    fun `verify federated entity resolver returns GraphQLError if exception is thrown during type resolution`() {
        val mockUserResolver: FederatedTypeResolver<User> = mockk {
            coEvery { resolve(any()) } throws RuntimeException("JUnit exception")
        }
        val resolver = EntityResolver(FederatedTypeRegistry(mapOf("User" to mockUserResolver)))
        val representations = listOf(mapOf<String, Any>("__typename" to "User", "userId" to 123, "name" to "testName"))
        val env: DataFetchingEnvironment = mockk {
            every { getArgument<Any>(any()) } returns representations
        }

        val result = resolver.get(env).get()
        verifyData(result.data, null)
        verifyErrors(result.errors, "Exception was thrown while trying to resolve federated type, representation={__typename=User, userId=123, name=testName}")
    }

    @Test
    fun `verify federated entity resolver processed representations in batches`() {
        val user1 = User(123, "testName1")
        val user2 = User(124, "testName2")
        val book = Book("988").apply {
            weight = 1.0
        }
        val representations = listOf(user1.toRepresentation(), book.toRepresentation(), user2.toRepresentation())
        val env = mockk<DataFetchingEnvironment> {
            every { getArgument<Any>(any()) } returns representations
        }

        val spyUserResolver = spyk(UserResolver())
        val spyBookResolver = spyk(BookResolver())
        val resolver = EntityResolver(FederatedTypeRegistry(mapOf("User" to spyUserResolver, "Book" to spyBookResolver)))
        val result = resolver.get(env).get()

        verifyData(result.data, user1, book, user2)
        verifyErrors(result.errors)

        coVerify {
            spyUserResolver.resolve(listOf(user1.toRepresentation(), user2.toRepresentation()))
            spyBookResolver.resolve(listOf(book.toRepresentation()))
        }
    }

    @Test
    fun `verify federated entity resolver returns both data and errors`() {
        val user = User(123, "testName")
        val book = Book("988").apply {
            weight = 1.0
        }
        val representations = listOf(user.toRepresentation(), book.toRepresentation())
        val env = mockk<DataFetchingEnvironment> {
            every { getArgument<Any>(any()) } returns representations
        }

        val spyUserResolver: UserResolver = spyk(UserResolver())
        val mockBookResolver: BookResolver = mockk {
            coEvery { resolve(any()) } throws RuntimeException("JUnit")
        }
        val resolver = EntityResolver(FederatedTypeRegistry(mapOf("User" to spyUserResolver, "Book" to mockBookResolver)))
        val result = resolver.get(env).get()

        verifyData(result.data, user, null)
        verifyErrors(result.errors, "Exception was thrown while trying to resolve federated type, representation={__typename=Book, id=988, weight=1.0}")

        coVerify {
            spyUserResolver.resolve(listOf(user.toRepresentation()))
            mockBookResolver.resolve(listOf(book.toRepresentation()))
        }
    }

    @Test
    fun `verify federated entity resolver returns error when different number of entities is returned than requested`() {
        val user = User(123, "testName1")
        val representations = listOf(user.toRepresentation(), user.toRepresentation())
        val env = mockk<DataFetchingEnvironment> {
            every { getArgument<Any>(any()) } returns representations
        }

        val mockUserResolver: UserResolver = mockk {
            coEvery { resolve(any()) } returns listOf(user)
        }
        val resolver = EntityResolver(FederatedTypeRegistry(mapOf("User" to mockUserResolver)))
        val result = resolver.get(env).get()

        verifyData(result.data, null, null)
        verifyErrors(result.errors,
            "Federation batch request for User generated different number of results than requested, representations=2, results=1",
            "Federation batch request for User generated different number of results than requested, representations=2, results=1")

        coVerify {
            mockUserResolver.resolve(listOf(user.toRepresentation(), user.toRepresentation()))
        }
    }

    private fun verifyData(data: List<Any?>, vararg expected: Any?) {
        assertEquals(expected.size, data.size)
        for ((index, entity) in data.withIndex()) {
            assertEquals(expected[index], entity)
        }
    }

    private fun verifyErrors(errors: List<GraphQLError>, vararg expectedErrors: String) {
        assertEquals(expectedErrors.size, errors.size)
        for ((index, error) in errors.withIndex()) {
            assertEquals(expectedErrors[index], error.message)
        }
    }

    private fun Book.toRepresentation() = mapOf("__typename" to "Book", "id" to id, "weight" to weight)
    private fun User.toRepresentation() = mapOf("__typename" to "User", "userId" to userId, "name" to name)
}
