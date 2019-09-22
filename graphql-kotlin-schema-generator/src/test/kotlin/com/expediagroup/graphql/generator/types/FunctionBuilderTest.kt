/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.annotations.GraphQLContext
import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLDirective
import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.exceptions.TypeNotSupportedException
import com.expediagroup.graphql.execution.FunctionDataFetcher
import graphql.ExceptionWhileDataFetching
import graphql.Scalars
import graphql.execution.DataFetcherResult
import graphql.execution.ExecutionPath
import graphql.introspection.Introspection
import graphql.language.SourceLocation
import graphql.schema.DataFetchingEnvironment
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLTypeUtil
import io.reactivex.Flowable
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateClass")
internal class FunctionBuilderTest : TypeTestHelper() {

    private lateinit var builder: FunctionBuilder

    override fun beforeTest() {
        builder = FunctionBuilder(generator)
    }

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

        fun context(@GraphQLContext context: String, string: String) = "$context and $string"

        fun ignoredParameter(color: String, @GraphQLIgnore ignoreMe: String) = "$color and $ignoreMe"

        fun publisher(num: Int): Publisher<Int> = Flowable.just(num)

        fun flowable(num: Int): Flowable<Int> = Flowable.just(num)

        fun completableFuture(num: Int): CompletableFuture<Int> = CompletableFuture.completedFuture(num)

        fun dataFetchingEnvironment(environment: DataFetchingEnvironment): String = environment.field.name

        fun dataFetcherResult(): DataFetcherResult<String> {
            val error = ExceptionWhileDataFetching(ExecutionPath.rootPath(), RuntimeException(), SourceLocation(1, 1))
            return DataFetcherResult.newResult<String>().data("Hello").error(error).build()
        }

        fun listDataFetcherResult(): DataFetcherResult<List<String>> = DataFetcherResult.newResult<List<String>>().data(listOf("Hello")).build()

        fun nullalbeListDataFetcherResult(): DataFetcherResult<List<String?>?> = DataFetcherResult.newResult<List<String?>?>().data(listOf("Hello")).build()

        fun dataFetcherCompletableFutureResult(): DataFetcherResult<CompletableFuture<String>> {
            val completedFuture = CompletableFuture.completedFuture("Hello")
            return DataFetcherResult.newResult<CompletableFuture<String>>().data(completedFuture).build()
        }

        fun completableFutureDataFetcherResult(): CompletableFuture<DataFetcherResult<String>> {
            val dataFetcherResult = DataFetcherResult.newResult<String>().data("Hello").build()
            return CompletableFuture.completedFuture(dataFetcherResult)
        }
    }

    @Test
    fun `Function description can be set`() {
        val kFunction = Happy::littleTrees
        val result = builder.function(kFunction, "Query", target = null, abstract = false)
        assertEquals("By bob", result.description)
    }

    @Test
    fun `Function names can be changed`() {
        val kFunction = Happy::originalName
        val result = builder.function(kFunction, "Query", target = null, abstract = false)
        assertEquals("renamedFunction", result.name)
    }

    @Test
    fun `Functions can be deprecated`() {
        val kFunction = Happy::sketch
        val result = builder.function(kFunction, "Query", target = null, abstract = false)
        assertTrue(result.isDeprecated)
        assertEquals("Should paint instead, replace with paint", result.deprecationReason)

        val fieldDirectives = result.directives
        assertEquals(1, fieldDirectives.size)
        assertEquals("deprecated", fieldDirectives.first().name)
    }

    @Test
    fun `test custom directive on function`() {
        val kFunction = Happy::littleTrees
        val result = builder.function(kFunction, "Query", target = null, abstract = false)

        assertEquals(1, result.directives.size)
        val directive = result.directives[0]
        assertEquals("functionDirective", directive.name)
        assertEquals("happy", directive.arguments[0].value)
        assertEquals("arg", directive.arguments[0].name)
        assertEquals(GraphQLNonNull(Scalars.GraphQLString), directive.arguments[0].type)
        assertEquals(
            directive.validLocations()?.toSet(),
            setOf(Introspection.DirectiveLocation.FIELD_DEFINITION)
        )
    }

    @Test
    fun `test context on argument`() {
        val kFunction = Happy::context
        val result = builder.function(kFunction, "Query", target = null, abstract = false)

        assertTrue(result.directives.isEmpty())
        assertEquals(expected = 1, actual = result.arguments.size)
        val arg = result.arguments.firstOrNull()
        assertEquals(expected = "string", actual = arg?.name)
    }

    @Test
    fun `test ignored parameter`() {
        val kFunction = Happy::ignoredParameter
        val result = builder.function(kFunction, "Query", target = null, abstract = false)

        assertTrue(result.directives.isEmpty())
        assertEquals(expected = 1, actual = result.arguments.size)
        val arg = result.arguments.firstOrNull()
        assertEquals(expected = "color", actual = arg?.name)
    }

    @Test
    fun `non-abstract function`() {
        val kFunction = MyInterface::printMessage
        val result = builder.function(fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertEquals(expected = 1, actual = result.arguments.size)
        assertTrue(generator.codeRegistry.getDataFetcher(FieldCoordinates.coordinates("Query", kFunction.name), result) is FunctionDataFetcher)
    }

    @Test
    fun `abstract function`() {
        val kFunction = MyInterface::printMessage
        val result = builder.function(fn = kFunction, parentName = "Query", target = null, abstract = true)

        assertEquals(expected = 1, actual = result.arguments.size)
    }

    @Test
    fun `abstract function with target`() {
        val kFunction = MyInterface::printMessage
        val result = builder.function(fn = kFunction, parentName = "Query", target = MyImplementation(), abstract = true)

        assertEquals(expected = 1, actual = result.arguments.size)
    }

    @Test
    fun `publisher return type is valid`() {
        val kFunction = Happy::publisher
        val result = builder.function(fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertEquals(expected = 1, actual = result.arguments.size)
        assertEquals("Int", (result.type as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `a return type that implements Publisher is valid`() {
        val kFunction = Happy::flowable
        val result = builder.function(fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertEquals(expected = 1, actual = result.arguments.size)
        assertEquals("Int", (result.type as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `completable future return type is valid`() {
        val kFunction = Happy::completableFuture
        val result = builder.function(fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertEquals(expected = 1, actual = result.arguments.size)
        assertEquals("Int", (result.type as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `DataFetchingEnvironment argument type is ignored`() {
        val kFunction = Happy::dataFetchingEnvironment
        val result = builder.function(fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertEquals(expected = 0, actual = result.arguments.size)
        assertEquals("String", (result.type as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `DataFetcherResult return type is valid and unwrapped in the schema`() {
        val kFunction = Happy::dataFetcherResult
        val result = builder.function(fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertEquals("String", (result.type as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `DataFetcherResult of a List is valid and unwrapped in the schema`() {
        val kFunction = Happy::listDataFetcherResult
        val result = builder.function(fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertTrue(result.type is GraphQLNonNull)
        val listType = GraphQLTypeUtil.unwrapNonNull(result.type)
        assertTrue(listType is GraphQLList)
        val stringType = GraphQLTypeUtil.unwrapNonNull(GraphQLTypeUtil.unwrapOne(listType))
        assertEquals("String", stringType.name)
    }

    @Test
    fun `DataFetcherResult of a nullable List is valid and unwrapped in the schema`() {
        val kFunction = Happy::nullalbeListDataFetcherResult
        val result = builder.function(fn = kFunction, parentName = "Query", target = null, abstract = false)

        val listType = result.type
        assertTrue(listType is GraphQLList)
        val stringType = listType.wrappedType
        assertEquals("String", stringType.name)
    }

    @Test
    fun `DataFetcherResult of a CompletableFuture is invalid`() {
        val kFunction = Happy::dataFetcherCompletableFutureResult

        assertFailsWith(TypeNotSupportedException::class) {
            builder.function(fn = kFunction, parentName = "Query", target = null, abstract = false)
        }
    }

    @Test
    fun `CompletableFuture of a DataFetcherResult is valid and unwrapped in the schema`() {
        val kFunction = Happy::completableFutureDataFetcherResult
        val result = builder.function(fn = kFunction, parentName = "Query", target = null, abstract = false)

        assertTrue(result.type is GraphQLNonNull)
        val stringType = GraphQLTypeUtil.unwrapNonNull(result.type)
        assertEquals("String", stringType.name)
    }

    @Test
    fun `Nested Self referencing object returns non null`() {
        val kInterfaceFunction = MyInterface::nestedReturnType
        val kInterfaceResult = builder.function(fn = kInterfaceFunction, parentName = "Query", target = null, abstract = false)


        val kImplFunction = MyImplementation::nestedReturnType
        val implResult = builder.function(fn = kImplFunction, parentName = "Query", target = null, abstract = false)

        assertTrue(implResult.type is GraphQLNonNull)
        assertEquals(kInterfaceResult.type, implResult.type)
    }
}
