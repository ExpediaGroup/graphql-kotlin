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

package com.expediagroup.graphql.generator

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLID
import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.exceptions.ConflictingTypesException
import com.expediagroup.graphql.exceptions.GraphQLKotlinException
import com.expediagroup.graphql.exceptions.InvalidIdTypeException
import com.expediagroup.graphql.extensions.deepName
import com.expediagroup.graphql.testSchemaConfig
import com.expediagroup.graphql.toSchema
import graphql.ExceptionWhileDataFetching
import graphql.GraphQL
import graphql.Scalars
import graphql.execution.DataFetcherResult
import graphql.execution.ExecutionPath
import graphql.language.SourceLocation
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import org.junit.jupiter.api.Test
import java.net.CookieManager
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateMember",
    "Detekt.FunctionOnlyReturningConstant",
    "Detekt.LargeClass",
    "Detekt.MethodOverloading")
class ToSchemaTest {
    @Test
    fun `SchemaGenerator generates a simple GraphQL schema`() {
        val schema = toSchema(
            queries = listOf(TopLevelObject(QueryObject())),
            mutations = listOf(TopLevelObject(MutationObject())),
            config = testSchemaConfig
        )
        val graphQL = GraphQL.newGraphQL(schema).build()

        val result = graphQL.execute(" { query(value: 1) { id } }")
        val geo: Map<String, Map<String, Any>>? = result.getData()

        assertEquals(1, geo?.get("query")?.get("id"))
    }

    @Test
    fun `SchemaGenerator generates a simple GraphQL schema with default builder`() {
        val schemaGenerator = SchemaGenerator(testSchemaConfig)
        val schema = generateSchema(
            generator = schemaGenerator,
            queries = listOf(TopLevelObject(QueryObject())),
            mutations = listOf(TopLevelObject(MutationObject())),
            subscriptions = emptyList()
        )

        val graphQL = GraphQL.newGraphQL(schema).build()

        val result = graphQL.execute(" { query(value: 1) { id } }")
        val geo: Map<String, Map<String, Any>>? = result.getData()

        assertEquals(1, geo?.get("query")?.get("id"))
    }

    @Test
    fun `Schema generator exposes arrays of primitive types as function arguments`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithArray())), config = testSchemaConfig)
        val firstArgumentType = schema.queryType.getFieldDefinition("sumOf").arguments[0].type.deepName
        assertEquals("[Int!]!", firstArgumentType)

        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.execute("{ sumOf(ints: [1, 2, 3]) }")
        val sum = result.getData<Map<String, Int>>().values.first()

        assertEquals(6, sum)
    }

    @Test
    fun `Schema generator exposes arrays of complex types as function arguments`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithArray())), config = testSchemaConfig)
        val firstArgumentType = schema.queryType.getFieldDefinition("sumOfComplexArray").arguments[0].type.deepName
        assertEquals("[ComplexWrappingTypeInput!]!", firstArgumentType)

        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.execute("{sumOfComplexArray(objects: [{value: 1}, {value: 2}, {value: 3}])}")
        val sum = result.getData<Map<String, Int>>().values.first()

        assertEquals(6, sum)
    }

    @Test
    fun `SchemaGenerator ignores fields and functions with @Ignore`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithIgnored())), config = testSchemaConfig)

        assertTrue(schema.queryType.fieldDefinitions.none {
            it.name == "ignoredFunction"
        })

        val resultType = schema.getObjectType("ResultWithIgnored")
        assertTrue(resultType.fieldDefinitions.none {
            it.name == "ignoredFunction"
        })

        assertTrue(resultType.fieldDefinitions.none {
            it.name == "ignoredProperty"
        })
    }

    @Test
    fun `SchemaGenerator generates a GraphQL schema with repeated types to test conflicts`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithRepeatedTypes())), config = testSchemaConfig)
        val resultType = schema.getObjectType("Result")
        val topLevelQuery = schema.getObjectType("Query")
        assertEquals("Result!", topLevelQuery.getFieldDefinition("query").type.deepName)
        assertEquals("SomeObject", resultType.getFieldDefinition("someObject").type.deepName)
        assertEquals("[Int!]!", resultType.getFieldDefinition("someIntValues").type.deepName)
        assertEquals("[Boolean!]!", resultType.getFieldDefinition("someBooleanValues").type.deepName)
        assertEquals("[SomeObject!]!", resultType.getFieldDefinition("someObjectValues").type.deepName)
        assertEquals("[SomeOtherObject!]!", resultType.getFieldDefinition("someOtherObjectValues").type.deepName)
    }

    @Test
    fun `SchemaGenerator generates a GraphQL schema with mixed nullity`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithNullableAndNonNullTypes())), config = testSchemaConfig)
        val resultType = schema.getObjectType("MixedNullityResult")
        val topLevelQuery = schema.getObjectType("Query")
        assertEquals("MixedNullityResult!", topLevelQuery.getFieldDefinition("query").type.deepName)
        assertEquals("String", resultType.getFieldDefinition("oneThing").type.deepName)
        assertEquals("String!", resultType.getFieldDefinition("theNextThing").type.deepName)
    }

    @Test
    fun `SchemaGenerator generates a GraphQL schema where the input types differ from the output types`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithInputObject())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("Query")
        assertEquals(
            "SomeObjectInput!",
            topLevelQuery.getFieldDefinition("query").getArgument("someObject").type.deepName
        )
        assertEquals("SomeObject!", topLevelQuery.getFieldDefinition("query").type.deepName)
    }

    @Test
    fun `SchemaGenerator generates a GraphQL schema where the input and output enum is the same`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithInputEnum())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("Query")
        assertEquals("SomeEnum!", topLevelQuery.getFieldDefinition("query").getArgument("someEnum").type.deepName)
        assertEquals("SomeEnum!", topLevelQuery.getFieldDefinition("query").type.deepName)
    }

    @Test
    fun `SchemaGenerator names types according to custom name in @GraphQLName`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithCustomName())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("Query")

        assertEquals("SomeInputObjectRenamedInput!", topLevelQuery.getFieldDefinition("query").getArgument("someInputObjectWithCustomName").type.deepName)
        assertEquals("SomeEnumRenamed!", topLevelQuery.getFieldDefinition("query").getArgument("someEnumWithCustomName").type.deepName)
        assertEquals("SomeObjectWithDefaultNameInput!", topLevelQuery.getFieldDefinition("query").getArgument("someObjectWithDefaultName").type.deepName)
        assertEquals("SomeOtherObjectRenamed!", topLevelQuery.getFieldDefinition("query").type.deepName)
    }

    @Test
    fun `SchemaGenerator names self-referencing types according to custom name in @GraphQLName`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QuerySelfReferencingWithCustomName())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("Query")
        val resultType = schema.getObjectType("ObjectSelfReferencingRenamed")

        assertEquals("ObjectSelfReferencingRenamed!", topLevelQuery.getFieldDefinition("query").type.deepName)
        assertEquals("ObjectSelfReferencingRenamed", resultType.getFieldDefinition("self").type.deepName)
    }

    @Test
    fun `SchemaGenerator documents types annotated with @Description`() {
        val schema = toSchema(
            queries = listOf(TopLevelObject(QueryObject())),
            mutations = listOf(TopLevelObject(MutationObject())),
            config = testSchemaConfig
        )
        val geo = schema.getObjectType("Geography")
        assertTrue(geo.description.startsWith("A place"))
    }

    @Test
    fun `SchemaGenerator documents arguments annotated with @Description`() {
        val schema = toSchema(
            queries = listOf(TopLevelObject(QueryObject())),
            mutations = listOf(TopLevelObject(MutationObject())),
            config = testSchemaConfig
        )
        val documentation = schema.queryType.fieldDefinitions.first().arguments.first().description
        assertEquals("A GraphQL value", documentation)
    }

    @Test
    fun `SchemaGenerator documents properties annotated with @Description`() {
        val schema = toSchema(
            queries = listOf(TopLevelObject(QueryObject())),
            mutations = listOf(TopLevelObject(MutationObject())),
            config = testSchemaConfig
        )
        val documentation = schema.queryType.fieldDefinitions.first().description
        assertEquals("A GraphQL query method", documentation)
    }

    @Test
    fun `SchemaGenerator can expose functions on result classes`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithDataThatContainsFunction())), config = testSchemaConfig)
        val resultWithFunction = schema.getObjectType("ResultWithFunction")
        val repeatFieldDefinition = resultWithFunction.getFieldDefinition("repeat")
        assertEquals("repeat", repeatFieldDefinition.name)
        assertEquals("Int!", repeatFieldDefinition.arguments.first().type.deepName)

        assertEquals("String!", repeatFieldDefinition.type.deepName)
    }

    @Test
    fun `SchemaGenerator can execute functions on result classes`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithDataThatContainsFunction())), config = testSchemaConfig)
        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.execute("{ query(something: \"thing\") { repeat(n: 3) } }")
        val data: Map<String, Map<String, Any>> = result.getData()

        assertEquals("thingthingthing", data["query"]?.get("repeat"))
    }

    @Test
    fun `SchemaGenerator ignores private fields`() {
        val schema =
            toSchema(queries = listOf(TopLevelObject(QueryWithPrivateParts())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("Query")
        val query = topLevelQuery.getFieldDefinition("query")
        val resultWithPrivateParts = query.type as? GraphQLObjectType
        assertNotNull(resultWithPrivateParts)
        assertEquals("ResultWithPrivateParts", resultWithPrivateParts.deepName)
        assertEquals(1, resultWithPrivateParts.fieldDefinitions.size)
        assertEquals("something", resultWithPrivateParts.fieldDefinitions[0].name)
    }

    @Test
    fun `SchemaGenerator throws when encountering java stdlib`() {
        assertFailsWith(GraphQLKotlinException::class) {
            toSchema(queries = listOf(TopLevelObject(QueryWithJavaClass())), config = testSchemaConfig)
        }
    }

    @Test
    fun `SchemaGenerator throws when encountering list of java stdlib`() {
        assertFailsWith(GraphQLKotlinException::class) {
            toSchema(queries = listOf(TopLevelObject(QueryWithListOfJavaClass())), config = testSchemaConfig)
        }
    }

    @Test
    fun `SchemaGenerator throws when encountering conflicting types`() {
        assertFailsWith(ConflictingTypesException::class) {
            toSchema(queries = listOf(TopLevelObject(QueryWithConflictingTypes())), config = testSchemaConfig)
        }
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun `SchemaGenerator supports type references`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithParentChildRelationship())), config = testSchemaConfig)

        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.execute("{ query { name children { name } } }")
        val data = result.getData<Map<String, Map<String, Any>>>()

        assertNotNull(data)
        val res: Map<String, Any>? = data["query"]
        assertEquals("Bob", res?.get("name").toString())
        val bobChildren = res?.get("children") as? List<Map<String, Any>>
        assertNotNull(bobChildren)

        val firstChild = bobChildren.first()
        assertEquals("Alice", firstChild["name"])
        assertNull(firstChild["children"])
    }

    @Test
    fun `SchemaGenerator support GraphQLID scalar`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithId())), config = testSchemaConfig)

        val placeType = schema.getObjectType("PlaceOfIds")
        assertEquals(Scalars.GraphQLID, (placeType.getFieldDefinition("id").type as? GraphQLNonNull)?.wrappedType)
    }

    @Test
    fun `SchemaGenerator throws an exception for invalid GraphQLID`() {
        assertFailsWith(InvalidIdTypeException::class) {
            toSchema(queries = listOf(TopLevelObject(QueryWithInvalidId())), config = testSchemaConfig)
        }
    }

    @Test
    fun `SchemaGenerator supports Scalar GraphQLID for input types`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryObject())), mutations = listOf(TopLevelObject(MutationWithId())), config = testSchemaConfig)

        val furnitureType = schema.getObjectType("Furniture")
        val serialField = furnitureType.getFieldDefinition("serial").type as? GraphQLNonNull
        assertEquals(Scalars.GraphQLID, serialField?.wrappedType)
    }

    @Test
    fun `SchemaGenerator supports DataFetcherResult as a return type`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithDataFetcherResult())), config = testSchemaConfig)

        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.execute("{ dataAndErrors }")
        val data = result.getData<Map<String, String>>()
        val errors = result.errors

        assertNotNull(data)
        val res: String? = data["dataAndErrors"]
        assertEquals(actual = res, expected = "Hello")

        assertNotNull(errors)
        assertEquals(expected = 1, actual = errors.size)
    }

    class QueryObject {
        @GraphQLDescription("A GraphQL query method")
        fun query(@GraphQLDescription("A GraphQL value") value: Int): Geography = Geography(value, GeoType.CITY, listOf())
    }

    class QueryWithArray {
        fun sumOf(ints: IntArray): Int = ints.sum()
        fun sumOfComplexArray(objects: Array<ComplexWrappingType>): Int = objects.map { it.value }.sum()
    }

    class QueryWithIgnored {
        fun query(): ResultWithIgnored? = null

        @GraphQLIgnore
        @Suppress("Detekt.FunctionOnlyReturningConstant")
        fun ignoredFunction() = "payNoAttentionToMe"
    }

    class ResultWithIgnored(val something: String) {
        @GraphQLIgnore
        val ignoredProperty = "payNoAttentionToMe"

        @GraphQLIgnore
        @Suppress("Detekt.FunctionOnlyReturningConstant")
        fun ignoredFunction() = "payNoAttentionToMe"
    }

    class MutationObject {
        fun mutation(value: Int): Boolean = value > 0
    }

    data class ComplexWrappingType(val value: Int)

    @GraphQLDescription("A place")
    data class Geography(
        val id: Int?,
        val type: GeoType,
        val locations: List<Location>
    )

    enum class GeoType {
        CITY, STATE
    }

    data class Location(val lat: Double, val lon: Double)

    class QueryWithRepeatedTypes {
        fun query(): Result =
            Result(
                listOf(),
                listOf(),
                listOf(),
                listOf(),
                SomeObject("something")
            )
    }

    data class Result(
        val someIntValues: List<Int>,
        val someBooleanValues: List<Boolean>,
        val someObjectValues: List<SomeObject>,
        val someOtherObjectValues: List<SomeOtherObject>,
        val someObject: SomeObject?
    )

    data class SomeObject(val name: String)
    data class SomeOtherObject(val name: String)

    class QueryWithNullableAndNonNullTypes {
        fun query(): MixedNullityResult =
            MixedNullityResult("hey", "ho")
    }

    data class MixedNullityResult(val oneThing: String?, val theNextThing: String)

    class QueryWithInputObject {
        fun query(someObject: SomeObject): SomeObject =
            SomeObject("someName")
    }

    class QueryWithInputEnum {
        fun query(someEnum: SomeEnum): SomeEnum =
            SomeEnum.SomeValue
    }

    enum class SomeEnum { SomeValue }

    class QueryWithDataThatContainsFunction {
        fun query(something: String): ResultWithFunction? =
            ResultWithFunction(something)
    }

    class ResultWithFunction(private val something: String) {
        fun repeat(n: Int) = something.repeat(n)
    }

    class QueryWithPrivateParts {
        fun query(something: String): ResultWithPrivateParts? = null
    }

    class ResultWithPrivateParts(val something: String) {

        private val privateSomething: String = "soPrivate"

        private fun privateFunction(): Int = 2
    }

    class QueryWithJavaClass {
        fun query(): java.net.CookieManager? = CookieManager()
    }

    class QueryWithListOfJavaClass {
        fun listQuery(): List<java.net.CookieManager> = listOf(CookieManager())
    }

    class QueryWithConflictingTypes {
        @GraphQLDescription("A conflicting GraphQL query method")
        fun type1() = GeoType.CITY

        @GraphQLDescription("A second conflicting GraphQL query method")
        fun type2() = com.expediagroup.graphql.test.conflicts.GeoType.CITY
    }

    class QueryWithParentChildRelationship {
        fun query(): Person {
            val children = listOf(Person("Alice"))
            return Person("Bob", children)
        }
    }

    data class Person(val name: String, val children: List<Person>? = null)

    data class PlaceOfIds(@GraphQLID val id: String)

    data class InvalidIds(@GraphQLID val person: Person)

    class QueryWithId {
        fun query(): PlaceOfIds = PlaceOfIds(UUID.randomUUID().toString())
    }

    class QueryWithInvalidId {
        fun query(): InvalidIds = InvalidIds(Person("person id not a valid type id"))
    }

    data class SomeObjectWithDefaultName(val title: String)

    @GraphQLName("SomeObjectRenamed")
    data class SomeObjectWithCustomName(val title: String)

    @GraphQLName("SomeOtherObjectRenamed")
    data class SomeOtherObjectWithCustomName(
        val title: String,
        val someObject: SomeObjectWithCustomName,
        val someEnum: SomeEnumWithCustomName
    )

    @GraphQLName("SomeInputObjectRenamed")
    data class SomeInputObjectWithCustomName(val title: String)

    @GraphQLName("SomeEnumRenamed")
    enum class SomeEnumWithCustomName { ONE, TWO }

    class QueryWithCustomName {
        fun query(
            someInputObjectWithCustomName: SomeInputObjectWithCustomName,
            someEnumWithCustomName: SomeEnumWithCustomName,
            someObjectWithDefaultName: SomeObjectWithDefaultName
        ): SomeOtherObjectWithCustomName =
            SomeOtherObjectWithCustomName(
                title = someObjectWithDefaultName.title,
                someObject = SomeObjectWithCustomName("something"),
                someEnum = someEnumWithCustomName
            )
    }

    @GraphQLName("ObjectSelfReferencingRenamed")
    data class ObjectSelfReferencingWithCustomName(
        val self: ObjectSelfReferencingWithCustomName? = null
    )

    class QuerySelfReferencingWithCustomName {
        fun query(): ObjectSelfReferencingWithCustomName = ObjectSelfReferencingWithCustomName()
    }

    class MutationWithId {
        fun mutate(furniture: Furniture): Furniture = furniture
    }

    data class Furniture(
        @GraphQLID val serial: String,
        val type: String
    )

    class QueryWithDataFetcherResult {
        fun dataAndErrors(): DataFetcherResult<String> {
            val error = ExceptionWhileDataFetching(ExecutionPath.rootPath(), RuntimeException(), SourceLocation(1, 1))
            return DataFetcherResult.newResult<String>().data("Hello").error(error).build()
        }
    }
}
