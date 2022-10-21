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

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.exceptions.TypeNotSupportedException
import com.expediagroup.graphql.generator.execution.FunctionDataFetcher
import com.expediagroup.graphql.generator.scalars.ID
import graphql.ExceptionWhileDataFetching
import graphql.Scalars
import graphql.Scalars.GraphQLInt
import graphql.Scalars.GraphQLString
import graphql.execution.DataFetcherResult
import graphql.execution.ResultPath
import graphql.introspection.Introspection
import graphql.language.SourceLocation
import graphql.schema.DataFetchingEnvironment
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLTypeUtil
import io.reactivex.rxjava3.core.Flowable
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateClass")
class GenerateFunctionTest : TypeTestHelper() {

    internal interface MyInterface {
        fun printMessage(message: String): String
        fun nestedReturnType(): MyImplementation
    }

    internal class MyImplementation : MyInterface {
        override fun printMessage(message: String): String = "message=$message"
        override fun nestedReturnType(): MyImplementation = MyImplementation()
    }

    @GraphQLDirective(locations = [Introspection.DirectiveLocation.FIELD_DEFINITION])
    internal annotation class FunctionDirective(val arg: String)

    private class Happy {

        @GraphQLDescription("By bob")
        @FunctionDirective("happy")
        fun littleTrees() = UUID.randomUUID().toString()

        @Deprecated("Should paint instead", ReplaceWith("paint"))
        fun sketch(tree: String) = tree

        @GraphQLName("renamedFunction")
        fun originalName(input: String) = input

        fun ignoredParameter(color: String, @GraphQLIgnore ignoreMe: String) = "$color and $ignoreMe"

        fun publisher(num: Int): Publisher<Int> = Flowable.just(num)

        fun flowable(num: Int): Flowable<Int> = Flowable.just(num)

        fun completableFuture(num: Int): CompletableFuture<Int> = CompletableFuture.completedFuture(num)

        fun dataFetchingEnvironment(environment: DataFetchingEnvironment): String = environment.field.name

        fun dataFetcherResult(): DataFetcherResult<String> {
            val error = ExceptionWhileDataFetching(ResultPath.rootPath(), RuntimeException(), SourceLocation(1, 1))
            return DataFetcherResult.newResult<String>().data("Hello").error(error).build()
        }

        fun listDataFetcherResult(): DataFetcherResult<List<String>> = DataFetcherResult.newResult<List<String>>().data(listOf("Hello")).build()

        fun nullableListDataFetcherResult(): DataFetcherResult<List<String?>?> = DataFetcherResult.newResult<List<String?>?>().data(listOf("Hello")).build()

        fun dataFetcherCompletableFutureResult(): DataFetcherResult<CompletableFuture<String>> {
            val completedFuture = CompletableFuture.completedFuture("Hello")
            return DataFetcherResult.newResult<CompletableFuture<String>>().data(completedFuture).build()
        }

        fun completableFutureDataFetcherResult(): CompletableFuture<DataFetcherResult<String>> {
            val dataFetcherResult = DataFetcherResult.newResult<String>().data("Hello").build()
            return CompletableFuture.completedFuture(dataFetcherResult)
        }

        fun randomId(): ID = ID(UUID.randomUUID().toString())
    }

    @Test
    fun `Function description can be set`() {
        val kFunction = Happy::littleTrees
        val result = generateFunction(generator, kFunction, "Query", target = null, abstract = false)
        assertEquals("By bob", result.description)
    }

    @Test
    fun `Function names can be changed`() {
        val kFunction = Happy::originalName
        val result = generateFunction(generator, kFunction, "Query", target = null, abstract = false)
        assertEquals("renamedFunction", result.name)
    }

    @Test
    fun `Functions can be deprecated`() {
        @Suppress("DEPRECATION")
        val kFunction = Happy::sketch
        val result = generateFunction(generator, kFunction, "Query", target = null, abstract = false)
        assertTrue(result.isDeprecated)
        assertEquals("Should paint instead, replace with paint", result.deprecationReason)

        val fieldDirectives = result.appliedDirectives
        assertEquals(1, fieldDirectives.size)
        assertEquals("deprecated", fieldDirectives.first().name)
    }

    @Test
    fun `test custom directive on function`() {
        val kFunction = Happy::littleTrees
        val result = generateFunction(generator, kFunction, "Query", target = null, abstract = false)

        assertEquals(1, result.appliedDirectives.size)
        val appliedDirective = result.appliedDirectives[0]
        assertEquals("functionDirective", appliedDirective.name)
        assertEquals("happy", appliedDirective.arguments[0].argumentValue.value)
        assertEquals("arg", appliedDirective.arguments[0].name)
        assertTrue(GraphQLNonNull(GraphQLString).isEqualTo(appliedDirective.arguments[0].type))

        val schemaDirective = generator.directives[appliedDirective.name]
        assertEquals(
            schemaDirective?.validLocations()?.toSet(),
            setOf(Introspection.DirectiveLocation.FIELD_DEFINITION)
        )
    }

    @Test
    fun `test ignored parameter`() {
        val kFunction = Happy::ignoredParameter
        val result = generateFunction(generator, kFunction, "Query", target = null, abstract = false)

        assertTrue(result.directives.isEmpty())
        assertEquals(expected = 1, actual = result.arguments.size)
        val arg = result.arguments.firstOrNull()
        assertEquals(expected = "color", actual = arg?.name)
    }

    @Test
    fun `non-abstract function`() {
        val kFunction = MyInterface::printMessage
        val result = generateFunction(generator, fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertEquals(expected = 1, actual = result.arguments.size)
        assertTrue(generator.codeRegistry.getDataFetcher(FieldCoordinates.coordinates("Query", kFunction.name), result) is FunctionDataFetcher)
    }

    @Test
    fun `abstract function`() {
        val kFunction = MyInterface::printMessage
        val result = generateFunction(generator, fn = kFunction, parentName = "Query", target = null, abstract = true)

        assertEquals(expected = 1, actual = result.arguments.size)
    }

    @Test
    fun `abstract function with target`() {
        val kFunction = MyInterface::printMessage
        val result = generateFunction(generator, fn = kFunction, parentName = "Query", target = MyImplementation(), abstract = true)

        assertEquals(expected = 1, actual = result.arguments.size)
    }

    @Test
    fun `publisher return type is valid`() {
        val kFunction = Happy::publisher
        val result = generateFunction(generator, fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertEquals(expected = 1, actual = result.arguments.size)
        assertEquals(GraphQLInt, (result.type as? GraphQLNonNull)?.wrappedType)
    }

    @Test
    fun `a return type that implements Publisher is valid`() {
        val kFunction = Happy::flowable
        val result = generateFunction(generator, fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertEquals(expected = 1, actual = result.arguments.size)
        assertEquals(GraphQLInt, (result.type as? GraphQLNonNull)?.wrappedType)
    }

    @Test
    fun `completable future return type is valid`() {
        val kFunction = Happy::completableFuture
        val result = generateFunction(generator, fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertEquals(expected = 1, actual = result.arguments.size)
        assertEquals(GraphQLInt, (result.type as? GraphQLNonNull)?.wrappedType)
    }

    @Test
    fun `DataFetchingEnvironment argument type is ignored`() {
        val kFunction = Happy::dataFetchingEnvironment
        val result = generateFunction(generator, fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertEquals(expected = 0, actual = result.arguments.size)
        assertEquals(GraphQLString, (result.type as? GraphQLNonNull)?.wrappedType)
    }

    @Test
    fun `DataFetcherResult return type is valid and unwrapped in the schema`() {
        val kFunction = Happy::dataFetcherResult
        val result = generateFunction(generator, fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertEquals(GraphQLString, (result.type as? GraphQLNonNull)?.wrappedType)
    }

    @Test
    fun `DataFetcherResult of a List is valid and unwrapped in the schema`() {
        val kFunction = Happy::listDataFetcherResult
        val result = generateFunction(generator, fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertTrue(result.type is GraphQLNonNull)
        val listType = GraphQLTypeUtil.unwrapNonNull(result.type)
        assertTrue(listType is GraphQLList)
        val stringType = GraphQLTypeUtil.unwrapNonNull(GraphQLTypeUtil.unwrapOne(listType))
        assertEquals(GraphQLString, stringType)
    }

    @Test
    fun `DataFetcherResult of a nullable List is valid and unwrapped in the schema`() {
        val kFunction = Happy::nullableListDataFetcherResult
        val result = generateFunction(generator, fn = kFunction, parentName = "Query", target = null, abstract = false)

        val listType = result.type
        assertTrue(listType is GraphQLList)
        val stringType = listType.wrappedType
        assertEquals(GraphQLString, stringType)
    }

    @Test
    fun `DataFetcherResult of a CompletableFuture is invalid`() {
        val kFunction = Happy::dataFetcherCompletableFutureResult

        assertFailsWith(TypeNotSupportedException::class) {
            generateFunction(generator, fn = kFunction, parentName = "Query", target = null, abstract = false)
        }
    }

    @Test
    fun `CompletableFuture of a DataFetcherResult is valid and unwrapped in the schema`() {
        val kFunction = Happy::completableFutureDataFetcherResult
        val result = generateFunction(generator, fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertTrue(result.type is GraphQLNonNull)
        val stringType = GraphQLTypeUtil.unwrapNonNull(result.type)
        assertEquals(GraphQLString, stringType)
    }

    @Test
    fun `Nested Self referencing object returns non null`() {
        val kInterfaceFunction = MyInterface::nestedReturnType
        val kInterfaceResult = generateFunction(generator, fn = kInterfaceFunction, parentName = "Query", target = null, abstract = false)
        val kImplFunction = MyImplementation::nestedReturnType
        val implResult = generateFunction(generator, fn = kImplFunction, parentName = "Query", target = null, abstract = false)

        assertTrue(implResult.type is GraphQLNonNull)
        val resultType = kInterfaceResult.type as? GraphQLNonNull
        assertNotNull(resultType)
        assertTrue(resultType.isEqualTo(implResult.type))
    }

    @Test
    fun `function can return GraphQL ID scalar`() {
        val kFunction = Happy::randomId
        val result = generateFunction(generator, kFunction, "Query", target = null, abstract = false)

        assertEquals("randomId", result.name)
        val returnType = GraphQLTypeUtil.unwrapAll(result.type)
        assertEquals(Scalars.GraphQLID, returnType)
    }
}
