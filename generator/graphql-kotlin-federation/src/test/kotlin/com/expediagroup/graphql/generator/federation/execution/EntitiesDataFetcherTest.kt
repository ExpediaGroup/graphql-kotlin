/*
 * Copyright 2022 Expedia, Inc
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

import com.expediagroup.graphql.generator.federation.data.AuthorResolver
import com.expediagroup.graphql.generator.federation.data.BookResolver
import com.expediagroup.graphql.generator.federation.data.UserResolver
import com.expediagroup.graphql.generator.federation.data.queries.federated.v1.Author
import com.expediagroup.graphql.generator.federation.data.queries.federated.v1.Book
import com.expediagroup.graphql.generator.federation.data.queries.federated.v1.User
import graphql.GraphQLContext
import graphql.GraphQLError
import graphql.schema.DataFetchingEnvironment
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EntitiesDataFetcherTest {
    @Test
    fun `verify can resolve federated entities`() {
        val resolver = EntitiesDataFetcher(UserResolver())
        val representations = listOf(
            mapOf<String, Any>("__typename" to "User", "userId" to 123, "name" to "testName")
        )
        val env = mockk<DataFetchingEnvironment> {
            every { getArgument<Any>(any()) } returns representations
            every { graphQlContext } returns GraphQLContext.newContext().build()
        }

        val result = resolver.get(env).get()
        verifyData(result.data, User(123, "testName"))
        verifyErrors(result.errors)
    }

    @Test
    fun `verify can resolve federated entities with promises`() {
        val resolver = EntitiesDataFetcher(AuthorResolver())
        val representations = listOf(
            mapOf<String, Any>("__typename" to "Author", "authorId" to 1)
        )
        val env = mockk<DataFetchingEnvironment> {
            every { getArgument<Any>(any()) } returns representations
        }
        val result = resolver.get(env).get()
        verifyData(result.data, Author(1, "Author 1"))
        verifyErrors(result.errors)
    }

    @Test
    fun `verify entities data fetcher returns GraphQLError if no resolver is found`() {
        val resolver = EntitiesDataFetcher()

        val representations = listOf(
            mapOf<String, Any>("__typename" to "User", "userId" to 123, "name" to "testName")
        )
        val env = mockk<DataFetchingEnvironment> {
            every { getArgument<Any>(any()) } returns representations
        }

        val result = resolver.get(env).get()
        verifyData(result.data, null)
        verifyErrors(result.errors, "Unable to resolve federated type, representation={__typename=User, userId=123, name=testName}")
    }

    @Test
    fun `verify entities data fetcher returns GraphQLError if __typename is not specified`() {
        val mockBookResolver = mockk<FederatedTypeSuspendResolver<*>> { every { typeName } returns "Book" }
        val mockUserResolver = mockk<FederatedTypeSuspendResolver<*>> { every { typeName } returns "User" }
        val resolver = EntitiesDataFetcher(mockBookResolver, mockUserResolver)
        val env = mockk<DataFetchingEnvironment> {
            every { getArgument<Any>(any()) } returns listOf(emptyMap<String, Any>())
            every { graphQlContext } returns GraphQLContext.newContext().build()
        }

        val result = resolver.get(env).get()
        verifyData(result.data, null)
        verifyErrors(result.errors, "Unable to resolve federated type, representation={}")
    }

    @Test
    fun `verify entities data fetcher returns GraphQLError if __typename cannot be resolved`() {
        val resolver = EntitiesDataFetcher(emptyList())
        val representations = listOf(mapOf<String, Any>("__typename" to "User", "userId" to 123, "name" to "testName"))
        val env = mockk<DataFetchingEnvironment> {
            every { getArgument<Any>(any()) } returns representations
            every { graphQlContext } returns GraphQLContext.newContext().build()
        }

        val result = resolver.get(env).get()
        verifyData(result.data, null)
        verifyErrors(result.errors, "Unable to resolve federated type, representation={__typename=User, userId=123, name=testName}")
    }

    @Test
    fun `verify entities data fetcher returns GraphQLError if exception is thrown during type resolution`() {
        val mockUserResolver: FederatedTypeSuspendResolver<User> = mockk {
            every { typeName } returns "User"
            coEvery { resolve(any(), any()) } throws RuntimeException("JUnit exception")
        }
        val resolver = EntitiesDataFetcher(listOf(mockUserResolver))
        val representations = listOf(mapOf<String, Any>("__typename" to "User", "userId" to 123, "name" to "testName"))
        val env: DataFetchingEnvironment = mockk {
            every { getArgument<Any>(any()) } returns representations
            every { graphQlContext } returns GraphQLContext.newContext().build()
        }

        val result = resolver.get(env).get()
        verifyData(result.data, null)
        verifyErrors(result.errors, "Exception was thrown while trying to resolve federated type, representation={__typename=User, userId=123, name=testName}")
    }

    @Test
    fun `verify entities data fetcher processed representations in batches`() {
        val user1 = User(123, "testName1")
        val user2 = User(124, "testName2")
        val book = Book("988").apply {
            weight = 1.0
        }
        val representations = listOf(user1.toRepresentation(), book.toRepresentation(), user2.toRepresentation())
        val env = mockk<DataFetchingEnvironment> {
            every { getArgument<Any>(any()) } returns representations
            every { graphQlContext } returns GraphQLContext.newContext().build()
        }

        val spyUserResolver = spyk(UserResolver())
        val spyBookResolver = spyk(BookResolver())
        val resolver = EntitiesDataFetcher(listOf(spyUserResolver, spyBookResolver))
        val result = resolver.get(env).get()

        verifyData(result.data, user1, book, user2)
        verifyErrors(result.errors)

        coVerify(exactly = 1) {
            spyUserResolver.resolve(any(), user1.toRepresentation())
            spyUserResolver.resolve(any(), user2.toRepresentation())
            spyBookResolver.resolve(any(), book.toRepresentation())
        }
    }

    @Test
    fun `verify entities data fetcher returns both data and errors`() {
        val user = User(123, "testName")
        val book = Book("988").apply {
            weight = 1.0
        }
        val representations = listOf(user.toRepresentation(), book.toRepresentation())
        val env = mockk<DataFetchingEnvironment> {
            every { getArgument<Any>(any()) } returns representations
            every { graphQlContext } returns GraphQLContext.newContext().build()
        }

        val spyUserResolver: UserResolver = spyk(UserResolver())
        val mockBookResolver: BookResolver = mockk {
            every { typeName } returns "Book"
            coEvery { resolve(any(), any()) } throws RuntimeException("JUnit")
        }
        val resolver = EntitiesDataFetcher(listOf(spyUserResolver, mockBookResolver))
        val result = resolver.get(env).get()

        verifyData(result.data, user, null)
        verifyErrors(result.errors, "Exception was thrown while trying to resolve federated type, representation={__typename=Book, id=988, weight=1.0}")

        coVerify {
            spyUserResolver.resolve(any(), user.toRepresentation())
            mockBookResolver.resolve(any(), book.toRepresentation())
        }
    }

    @Test
    fun `verify entities data fetcher can process suspend and promise resolvers asynchronously`() {
        val resolver = EntitiesDataFetcher(UserResolver(), AuthorResolver())
        val representations = listOf(
            mapOf<String, Any>("__typename" to "User", "userId" to 123, "name" to "testName"),
            mapOf<String, Any>("__typename" to "User", "userId" to 456, "name" to "testName 2"),
            mapOf<String, Any>("__typename" to "Author", "authorId" to 1),
            mapOf<String, Any>("__typename" to "Author", "authorId" to 2)
        )

        val env = mockk<DataFetchingEnvironment> {
            every { getArgument<Any>(any()) } returns representations
            every { graphQlContext } returns GraphQLContext.newContext().build()
        }

        val result = resolver.get(env).get()
        verifyData(
            result.data,
            User(123, "testName"),
            User(456, "testName 2"),
            Author(1, "Author 1"),
            Author(2, "Author 2"),
        )
        verifyErrors(result.errors)
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
    private fun Author.toRepresentation() = mapOf("__typename" to "Author", "authorId" to authorId)
}
