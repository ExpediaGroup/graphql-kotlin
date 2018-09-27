package com.expedia.graphql.schema

import com.expedia.graphql.TopLevelObjectDef
import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.annotations.GraphQLIgnore
import com.expedia.graphql.schema.exceptions.ConflictingTypesException
import com.expedia.graphql.schema.extensions.deepName
import com.expedia.graphql.toSchema
import graphql.GraphQL
import graphql.introspection.Introspection.DirectiveLocation.FIELD
import graphql.introspection.Introspection.DirectiveLocation.FIELD_DEFINITION
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import java.net.CookieManager
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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
    fun `SchemaGenerator ignores fields and functions with @Ignore`() {
        val schema =
            toSchema(listOf(TopLevelObjectDef(QueryWithIgnored())), config = testSchemaConfig)

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
        val schema =
            toSchema(listOf(TopLevelObjectDef(QueryWithRepeatedTypes())), config = testSchemaConfig)
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
        val schema =
            toSchema(listOf(TopLevelObjectDef(QueryWithNullableAndNonNullTypes())), config = testSchemaConfig)
        val resultType = schema.getObjectType("MixedNullityResult")
        val topLevelQuery = schema.getObjectType("TopLevelQuery")
        assertEquals("MixedNullityResult!", topLevelQuery.getFieldDefinition("query").type.deepName)
        assertEquals("String", resultType.getFieldDefinition("oneThing").type.deepName)
        assertEquals("String!", resultType.getFieldDefinition("theNextThing").type.deepName)
    }

    @Test
    fun `SchemaGenerator generates a GraphQL schema where the input types differ from the output types`() {
        val schema =
            toSchema(listOf(TopLevelObjectDef(QueryWithInputObject())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("TopLevelQuery")
        assertEquals(
            "SomeObjectInput!",
            topLevelQuery.getFieldDefinition("query").getArgument("someObject").type.deepName
        )
        assertEquals("SomeObject!", topLevelQuery.getFieldDefinition("query").type.deepName)
    }

    @Test
    fun `SchemaGenerator generates a GraphQL schema where the input and output enum is the same`() {
        val schema =
            toSchema(listOf(TopLevelObjectDef(QueryWithInputEnum())), config = testSchemaConfig)
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
        val schema =
            toSchema(listOf(TopLevelObjectDef(QueryWithDataThatContainsFunction())), config = testSchemaConfig)
        val resultWithFunction = schema.getObjectType("ResultWithFunction")
        val repeatFieldDefinition = resultWithFunction.getFieldDefinition("repeat")
        assertEquals("repeat", repeatFieldDefinition.name)
        assertEquals("Int!", repeatFieldDefinition.arguments.first().type.deepName)
        assertEquals("String!", repeatFieldDefinition.type.deepName)
    }

    @Test
    fun `SchemaGenerator can execute functions on result classes`() {
        val schema =
            toSchema(listOf(TopLevelObjectDef(QueryWithDataThatContainsFunction())), config = testSchemaConfig)
        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.execute("{ query(something: \"thing\") { repeat(n: 3) } }")
        val data: Map<String, Map<String, Any>> = result.getData()
        assertEquals("thingthingthing", data["query"]?.get("repeat"))
    }

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
    fun `SchemaGenerator marks deprecated queries`() {
        val schema =
            toSchema(listOf(TopLevelObjectDef(QueryWithDeprecatedFields())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("TopLevelQuery")
        val query = topLevelQuery.getFieldDefinition("deprecatedQuery")
        assertTrue(query.isDeprecated)
        assertEquals("this query is deprecated", query.deprecationReason)
    }

    @Test
    fun `SchemaGenerator marks deprecated queries and documents replacement`() {
        val schema =
            toSchema(listOf(TopLevelObjectDef(QueryWithDeprecatedFields())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("TopLevelQuery")
        val query = topLevelQuery.getFieldDefinition("deprecatedQueryWithReplacement")
        assertTrue(query.isDeprecated)
        assertEquals("this query is also deprecated, replace with shinyNewQuery", query.deprecationReason)
    }

    @Test
    fun `SchemaGenerator marks deprecated fields in the return objects`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithDeprecatedFields())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("TopLevelQuery")
        val query = topLevelQuery.getFieldDefinition("deprecatedFieldQuery")
        val result = (query.type as? GraphQLNonNull)?.wrappedType as? GraphQLObjectType
        val deprecatedField = result?.getFieldDefinition("deprecatedField")

        assertTrue(deprecatedField?.isDeprecated == true)
        assertEquals("Directives: deprecated", deprecatedField?.description)
        assertEquals("this field is deprecated", deprecatedField?.deprecationReason)
    }

    @Test
    fun `SchemaGenerator includes deprecated notice for deprecated fields`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithDeprecatedFields())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("TopLevelQuery")
        val query = topLevelQuery.getFieldDefinition("deprecatedArgumentQuery")
        val argument = query.getArgument("input")
        val deprecatedInputField = ((argument.type as? GraphQLNonNull)?.wrappedType as? GraphQLInputObjectType)?.getFieldDefinition("deprecatedField")
        assertEquals("Directives: deprecated", deprecatedInputField?.description)
    }

    @Test(expected = RuntimeException::class)
    fun `SchemaGenerator throws when encountering java stdlib`() {
        toSchema(listOf(TopLevelObjectDef(QueryWithJavaClass())), config = testSchemaConfig)
    }

    @Test(expected = ConflictingTypesException::class)
    fun `SchemaGenerator throws when encountering conflicting types`() {
        toSchema(queries = listOf(TopLevelObjectDef(QueryWithConflictingTypes())), config = testSchemaConfig)
    }

    @Test
    fun `SchemaGenerator can expose an interface and its implementations`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithInterface())), config = testSchemaConfig)

        val interfaceType = schema.getType("AnInterface")
        assertNotNull(interfaceType)

        val implementationType = schema.getObjectType("AnImplementation")
        assertNotNull(implementationType)
        assertEquals(1, implementationType.interfaces.size)
        assertEquals(implementationType.interfaces.first(), interfaceType)
    }

    @Test
    @Suppress("Detekt.UnsafeCast")
    fun `SchemaGenerator creates directives`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryObject())), config = testSchemaConfig)

        val geographyType = schema.getType("Geography") as? GraphQLObjectType
        assertNotNull(geographyType?.getDirective("whatever"))
        assertNotNull(geographyType?.getFieldDefinition("somethingCool")?.getDirective("directiveOnFunction"))
        assertNotNull((schema.getType("Location") as? GraphQLObjectType)?.getDirective("renamedDirective"))
        assertNotNull(schema.getDirective("whatever"))
        assertNotNull(schema.getDirective("renamedDirective"))
        val directiveOnFunction = schema.getDirective("directiveOnFunction")
        assertNotNull(directiveOnFunction)
        assertEquals(directiveOnFunction.validLocations()?.toSet(), setOf(FIELD_DEFINITION, FIELD))
    }

    @GraphQLDirective
    annotation class Whatever

    @GraphQLDirective(locations = [FIELD_DEFINITION, FIELD])
    annotation class DirectiveOnFunction

    @GraphQLDirective(name = "RenamedDirective")
    annotation class RenamedDirective(val x: Boolean)

    class QueryObject {
        @GraphQLDescription("A GraphQL query method")
        fun query(@GraphQLDescription("A GraphQL value") value: Int): Geography = Geography(value, GeoType.CITY, listOf())
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

    class QueryWithInterface {
        fun query(): AnInterface = AnImplementation()
    }

    interface AnInterface {
        val property: String
    }

    data class AnImplementation(
        override val property: String = "A value",
        val implementationSpecific: String = "It's implementation specific"
    ) : AnInterface

    class MutationObject {
        fun mutation(value: Int): Boolean = value > 0
    }

    @GraphQLDescription("A place")
    @Whatever
    data class Geography(
        val id: Int?,
        val type: GeoType,
        val locations: List<Location>
    ) {
        @DirectiveOnFunction
        fun somethingCool(): String = "Something cool"
    }

    enum class GeoType {
        CITY, STATE
    }

    @RenamedDirective(x = false)
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

    data class ResultWithFunction(val something: String) {
        fun repeat(n: Int) = something.repeat(n)
    }

    class QueryWithPrivateParts {
        fun query(something: String): ResultWithPrivateParts? = null
    }

    data class ResultWithPrivateParts(val something: String) {

        private val privateSomething: String = "soPrivate"

        private fun privateFunction(): Int = 2
    }

    class QueryWithDeprecatedFields {
        @Deprecated("this query is deprecated")
        fun deprecatedQuery(something: String) = something

        @Deprecated("this query is also deprecated", replaceWith = ReplaceWith("shinyNewQuery"))
        fun deprecatedQueryWithReplacement(something: String) = something

        fun deprecatedFieldQuery(something: String) = ClassWithDeprecatedField(something, something.reversed())

        fun deprecatedArgumentQuery(input: ClassWithDeprecatedField) = input.something
    }

    data class ClassWithDeprecatedField(
        val something: String,
        @Deprecated("this field is deprecated")
        val deprecatedField: String
    )

    class QueryWithJavaClass {
        fun query(): java.net.CookieManager? = CookieManager()
    }

    class QueryWithConflictingTypes {
        @GraphQLDescription("A conflicting GraphQL query method")
        fun type1() = GeoType.CITY

        @GraphQLDescription("A second conflicting GraphQL query method")
        fun type2() = com.expedia.graphql.conflicts.GeoType.CITY
    }
}
