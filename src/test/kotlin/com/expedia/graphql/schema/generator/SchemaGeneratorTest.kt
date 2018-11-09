package com.expedia.graphql.schema.generator

import com.expedia.graphql.TopLevelObjectDef
import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLID
import com.expedia.graphql.annotations.GraphQLIgnore
import com.expedia.graphql.schema.exceptions.ConflictingTypesException
import com.expedia.graphql.schema.exceptions.InvalidSchemaException
import com.expedia.graphql.schema.extensions.deepName
import com.expedia.graphql.schema.testSchemaConfig
import com.expedia.graphql.toSchema
import graphql.GraphQL
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import java.net.CookieManager
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateMember", "Detekt.FunctionOnlyReturningConstant", "Detekt.LargeClass")
class SchemaGeneratorTest {
    @Test
    fun `SchemaGenerator generates a simple GraphQL schema`() {
        val schema = toSchema(
            listOf(TopLevelObjectDef(QueryObject())),
            listOf(TopLevelObjectDef(MutationObject())),
            config = testSchemaConfig
        )
        val graphQL = GraphQL.newGraphQL(schema).build()

        val result = graphQL.execute(" { query(value: 1) { id } }")
        val geo: Map<String, Map<String, Any>>? = result.getData()

        assertEquals(1, geo?.get("query")?.get("id"))
    }

    @Test
    fun `SchemaGenerator throws exception on a mutation only schema`() {
        assertFailsWith(InvalidSchemaException::class) {
            toSchema(
                emptyList(),
                listOf(TopLevelObjectDef(MutationObject())),
                config = testSchemaConfig
            )
        }
    }

    @Test
    fun `Schema generator exposes arrays of primitive types as function arguments`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithArray())), config = testSchemaConfig)
        val firstArgumentType = schema.queryType.getFieldDefinition("sumOf").arguments[0].type.deepName
        assertEquals("[Int!]!", firstArgumentType)

        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.execute("{ sumOf(ints: [1, 2, 3]) }")
        val sum = result.getData<Map<String, Int>>().values.first()

        assertEquals(6, sum)
    }

    @Test
    fun `Schema generator exposes arrays of complex types as function arguments`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithArray())), config = testSchemaConfig)
        val firstArgumentType = schema.queryType.getFieldDefinition("sumOfComplexArray").arguments[0].type.deepName
        assertEquals("[ComplexWrappingTypeInput!]!", firstArgumentType)

        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.execute("{sumOfComplexArray(objects: [{value: 1}, {value: 2}, {value: 3}])}")
        val sum = result.getData<Map<String, Int>>().values.first()

        assertEquals(6, sum)
    }

    @Test
    fun `SchemaGenerator ignores fields and functions with @Ignore`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithIgnored())), config = testSchemaConfig)

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
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithRepeatedTypes())), config = testSchemaConfig)
        val resultType = schema.getObjectType("Result")
        val topLevelQuery = schema.getObjectType("TopLevelQuery")
        assertEquals("Result!", topLevelQuery.getFieldDefinition("query").type.deepName)
        assertEquals("SomeObject", resultType.getFieldDefinition("someObject").type.deepName)
        assertEquals("[Int!]!", resultType.getFieldDefinition("someIntValues").type.deepName)
        assertEquals("[Boolean!]!", resultType.getFieldDefinition("someBooleanValues").type.deepName)
        assertEquals("[SomeObject!]!", resultType.getFieldDefinition("someObjectValues").type.deepName)
        assertEquals("[SomeOtherObject!]!", resultType.getFieldDefinition("someOtherObjectValues").type.deepName)
    }

    @Test
    fun `SchemaGenerator generates a GraphQL schema with mixed nullity`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithNullableAndNonNullTypes())), config = testSchemaConfig)
        val resultType = schema.getObjectType("MixedNullityResult")
        val topLevelQuery = schema.getObjectType("TopLevelQuery")
        assertEquals("MixedNullityResult!", topLevelQuery.getFieldDefinition("query").type.deepName)
        assertEquals("String", resultType.getFieldDefinition("oneThing").type.deepName)
        assertEquals("String!", resultType.getFieldDefinition("theNextThing").type.deepName)
    }

    @Test
    fun `SchemaGenerator generates a GraphQL schema where the input types differ from the output types`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithInputObject())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("TopLevelQuery")
        assertEquals(
            "SomeObjectInput!",
            topLevelQuery.getFieldDefinition("query").getArgument("someObject").type.deepName
        )
        assertEquals("SomeObject!", topLevelQuery.getFieldDefinition("query").type.deepName)
    }

    @Test
    fun `SchemaGenerator generates a GraphQL schema where the input and output enum is the same`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithInputEnum())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("TopLevelQuery")
        assertEquals("SomeEnum!", topLevelQuery.getFieldDefinition("query").getArgument("someEnum").type.deepName)
        assertEquals("SomeEnum!", topLevelQuery.getFieldDefinition("query").type.deepName)
    }

    @Test
    fun `SchemaGenerator documents types annotated with @Description`() {
        val schema = toSchema(
            listOf(TopLevelObjectDef(QueryObject())),
            listOf(TopLevelObjectDef(MutationObject())),
            config = testSchemaConfig
        )
        val geo = schema.getObjectType("Geography")
        assertTrue(geo.description.startsWith("A place"))
    }

    @Test
    fun `SchemaGenerator documents arguments annotated with @Description`() {
        val schema = toSchema(
            listOf(TopLevelObjectDef(QueryObject())),
            listOf(TopLevelObjectDef(MutationObject())),
            config = testSchemaConfig
        )
        val documentation = schema.queryType.fieldDefinitions.first().arguments.first().description
        assertEquals("A GraphQL value", documentation)
    }

    @Test
    fun `SchemaGenerator documents properties annotated with @Description`() {
        val schema = toSchema(
            listOf(TopLevelObjectDef(QueryObject())),
            listOf(TopLevelObjectDef(MutationObject())),
            config = testSchemaConfig
        )
        val documentation = schema.queryType.fieldDefinitions.first().description
        assertEquals("A GraphQL query method", documentation)
    }

    @Test
    fun `SchemaGenerator can expose functions on result classes`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithDataThatContainsFunction())), config = testSchemaConfig)
        val resultWithFunction = schema.getObjectType("ResultWithFunction")
        val repeatFieldDefinition = resultWithFunction.getFieldDefinition("repeat")
        assertEquals("repeat", repeatFieldDefinition.name)
        assertEquals("Int!", repeatFieldDefinition.arguments.first().type.deepName)

        assertEquals("String!", repeatFieldDefinition.type.deepName)
    }

    @Test
    fun `SchemaGenerator can execute functions on result classes`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithDataThatContainsFunction())), config = testSchemaConfig)
        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.execute("{ query(something: \"thing\") { repeat(n: 3) } }")
        val data: Map<String, Map<String, Any>> = result.getData()

        assertEquals("thingthingthing", data["query"]?.get("repeat"))
    }

    @Suppress("Detekt.UnsafeCast")
    @Test
    fun `SchemaGenerator ignores private fields`() {
        val schema =
            toSchema(listOf(TopLevelObjectDef(QueryWithPrivateParts())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("TopLevelQuery")
        val query = topLevelQuery.getFieldDefinition("query")
        val resultWithPrivateParts = query.type as GraphQLObjectType
        assertEquals("ResultWithPrivateParts", resultWithPrivateParts.deepName)
        assertEquals(1, resultWithPrivateParts.fieldDefinitions.size)
        assertEquals("something", resultWithPrivateParts.fieldDefinitions[0].name)
    }

    @Test
    fun `SchemaGenerator throws when encountering java stdlib`() {
        assertThrows(RuntimeException::class.java) {
            toSchema(listOf(TopLevelObjectDef(QueryWithJavaClass())), config = testSchemaConfig)
        }
    }

    @Test
    fun `SchemaGenerator throws when encountering conflicting types`() {
        assertThrows(ConflictingTypesException::class.java) {
            toSchema(queries = listOf(TopLevelObjectDef(QueryWithConflictingTypes())), config = testSchemaConfig)
        }
    }

    @Test
    fun `SchemaGenerator should throw exception if no queries and no mutations are specified`() {
        assertThrows(InvalidSchemaException::class.java) {
            toSchema(emptyList(), emptyList(), config = testSchemaConfig)
        }
    }

    @Test
    fun `SchemaGenerator supports type references`() {
        val schema = toSchema(queries = listOf(TopLevelObjectDef(QueryWithParentChildRelationship())), config = testSchemaConfig)

        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.execute("{ query { name children { name } } }")
        val data = result.getData<Map<String, Map<String, Any>>>()

        assertNotNull(data)
        val res = data["query"]
        assertEquals("Bob", res?.get("name").toString())
        val bobChildren = res?.get("children") as? List<Map<String, Any>>
        assertNotNull(bobChildren)

        val firstChild = bobChildren?.get(0)
        assertEquals("Alice", firstChild?.get("name"))
        assertNull(firstChild?.get("children"))
    }

    @Test
    fun `SchemaGenerator support GraphQLID scalar`() {
        val schema = toSchema(queries = listOf(TopLevelObjectDef(QueryWithId())), config = testSchemaConfig)

        val placeType = schema.getObjectType("PlaceOfIds")
        assertEquals(graphql.Scalars.GraphQLID, (placeType.getFieldDefinition("intId").type as? GraphQLNonNull)?.wrappedType)
        assertEquals(graphql.Scalars.GraphQLID, (placeType.getFieldDefinition("longId").type as? GraphQLNonNull)?.wrappedType)
        assertEquals(graphql.Scalars.GraphQLID, (placeType.getFieldDefinition("stringId").type as? GraphQLNonNull)?.wrappedType)
        assertEquals(graphql.Scalars.GraphQLID, (placeType.getFieldDefinition("uuid").type as? GraphQLNonNull)?.wrappedType)
    }

    @Test
    fun `SchemaGenerator throws an exception for invalid GraphQLID`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            toSchema(queries = listOf(TopLevelObjectDef(QueryWithInvalidId())), config = testSchemaConfig)
        }

        assertEquals("Person is not a valid ID type, only [kotlin.Int, kotlin.String, kotlin.Long, java.util.UUID] are accepted", exception.message)
    }

    class QueryObject {
        @GraphQLDescription("A GraphQL query method")
        fun query(@GraphQLDescription("A GraphQL value") value: Int): Geography = Geography(value, GeoType.CITY, listOf())
    }

    class QueryWithArray {
        fun sumOf(ints: Array<Int>): Int = ints.sum()
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

    class QueryWithConflictingTypes {
        @GraphQLDescription("A conflicting GraphQL query method")
        fun type1() = GeoType.CITY

        @GraphQLDescription("A second conflicting GraphQL query method")
        fun type2() = com.expedia.graphql.conflicts.GeoType.CITY
    }

    class QueryWithParentChildRelationship {
        fun query(): Person {
            val children = listOf(Person("Alice"))
            return Person("Bob", children)
        }
    }

    data class Person(val name: String, val children: List<Person>? = null)

    data class PlaceOfIds(
        @property:GraphQLID val intId: Int,
        @property:GraphQLID val longId: Long,
        @property:GraphQLID val stringId: String,
        @property:GraphQLID val uuid: UUID
    )

    data class InvalidIds(@property:GraphQLID val person: Person)

    class QueryWithId {
        fun query(): PlaceOfIds = PlaceOfIds(42, 24, "42", UUID.randomUUID())
    }

    class QueryWithInvalidId {
        fun query(): InvalidIds = InvalidIds(Person("person id not a valid type id"))
    }
}
