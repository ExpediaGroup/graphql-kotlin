package com.expedia.graphql.schema.generator

import com.expedia.graphql.TopLevelObjectDef
import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.schema.testSchemaConfig
import com.expedia.graphql.toSchema
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
        assertEquals(directiveOnFunction.validLocations()?.toSet(), setOf(Introspection.DirectiveLocation.FIELD_DEFINITION, Introspection.DirectiveLocation.FIELD))
    }
}

@GraphQLDirective
annotation class Whatever

@GraphQLDirective(locations = [Introspection.DirectiveLocation.FIELD_DEFINITION, Introspection.DirectiveLocation.FIELD])
annotation class DirectiveOnFunction

@GraphQLDirective(name = "RenamedDirective")
annotation class RenamedDirective(val x: Boolean)

@Whatever
class Geography(
    val id: Int?,
    val type: GeoType,
    val locations: List<Location>
) {
    @Suppress("Detekt.FunctionOnlyReturningConstant")
    @DirectiveOnFunction
    fun somethingCool(): String = "Something cool"
}

enum class GeoType {
    CITY, STATE
}

@RenamedDirective(x = false)
data class Location(val lat: Double, val lon: Double)

class QueryObject {
    fun query(value: Int): Geography = Geography(value, GeoType.CITY, listOf())
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
