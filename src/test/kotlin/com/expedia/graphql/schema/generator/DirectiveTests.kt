package com.expedia.graphql.schema.generator

import com.expedia.graphql.TopLevelObjectDef
import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.schema.testSchemaConfig
import com.expedia.graphql.toSchema
import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DirectiveTests {
    @Test
    fun `SchemaGenerator marks deprecated fields in the return objects`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithDeprecatedFields())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("TopLevelQuery")
        val query = topLevelQuery.getFieldDefinition("deprecatedFieldQuery")
        val result = (query.type as? GraphQLNonNull)?.wrappedType as? GraphQLObjectType
        val deprecatedField = result?.getFieldDefinition("deprecatedField")

        assertEquals(deprecatedField?.isDeprecated, true)
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

    @Test
    fun `SchemaGenerator marks deprecated queries and documents replacement`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithDeprecatedFields())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("TopLevelQuery")
        val query = topLevelQuery.getFieldDefinition("deprecatedQueryWithReplacement")

        assertTrue(query.isDeprecated)
        assertEquals("this query is also deprecated, replace with shinyNewQuery", query.deprecationReason)
    }

    @Test
    fun `SchemaGenerator marks deprecated queries`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithDeprecatedFields())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("TopLevelQuery")
        val query = topLevelQuery.getFieldDefinition("deprecatedQuery")
        assertTrue(query.isDeprecated)
        assertEquals("this query is deprecated", query.deprecationReason)
    }

    @Test
    @Suppress("Detekt.UnsafeCast")
    fun `Directive renaming`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryObject())), config = testSchemaConfig)

        val renamedDirective = assertNotNull(
            (schema.getType("Location") as? GraphQLObjectType)
                ?.getDirective("rightNameDirective")
        )

        assertEquals("arenaming", renamedDirective.arguments[0].value)
        assertEquals("arg", renamedDirective.arguments[0].name)
        assertEquals(Scalars.GraphQLString, renamedDirective.arguments[0].type)
    }

    @Test
    @Suppress("Detekt.UnsafeCast")
    fun `Directives on classes`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryObject())), config = testSchemaConfig)

        val directive = assertNotNull(
            (schema.getType("Geography") as? GraphQLObjectType)
                ?.getDirective("onClassDirective")
        )

        assertEquals("aclass", directive.arguments[0].value)
        assertEquals("arg", directive.arguments[0].name)
        assertEquals(Scalars.GraphQLString, directive.arguments[0].type)
    }

    @Test
    @Suppress("Detekt.UnsafeCast")
    fun `Directives on functions`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryObject())), config = testSchemaConfig)

        val directive = assertNotNull(
            (schema.getType("Geography") as? GraphQLObjectType)
                ?.getFieldDefinition("somethingCool")
                ?.getDirective("onFunctionDirective")
        )

        assertEquals("afunction", directive.arguments[0].value)
        assertEquals("arg", directive.arguments[0].name)
        assertEquals(Scalars.GraphQLString, directive.arguments[0].type)

        assertNotNull(directive)
        assertEquals(
            directive.validLocations()?.toSet(),
            setOf(Introspection.DirectiveLocation.FIELD_DEFINITION, Introspection.DirectiveLocation.FIELD)
        )
    }

    @Test
    @Suppress("Detekt.UnsafeCast")
    fun `Directives on arguments`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryObject())), config = testSchemaConfig)

        val directive = assertNotNull(
            schema.queryType
                .getFieldDefinition("query")
                .getArgument("value")
                .getDirective("onArgumentDirective")
        )

        assertEquals("anargument", directive.arguments[0].value)
        assertEquals("arg", directive.arguments[0].name)
        assertEquals(Scalars.GraphQLString, directive.arguments[0].type)
    }
}

@GraphQLDirective(name = "RightNameDirective")
annotation class WrongNameDirective(val arg: String)

@GraphQLDirective
annotation class OnClassDirective(val arg: String)

@GraphQLDirective
annotation class OnArgumentDirective(val arg: String)

@GraphQLDirective(locations = [Introspection.DirectiveLocation.FIELD_DEFINITION, Introspection.DirectiveLocation.FIELD])
annotation class OnFunctionDirective(val arg: String)

@OnClassDirective(arg = "aclass")
class Geography(
    val id: Int?,
    val type: GeoType,
    val locations: List<Location>
) {
    @Suppress("Detekt.FunctionOnlyReturningConstant")
    @OnFunctionDirective(arg = "afunction")
    fun somethingCool(): String = "Something cool"
}

enum class GeoType {
    CITY, STATE
}

@WrongNameDirective(arg = "arenaming")
data class Location(val lat: Double, val lon: Double)

class QueryObject {
    fun query(@OnArgumentDirective(arg = "anargument") value: Int): Geography = Geography(value, GeoType.CITY, listOf())
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
