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

package com.expediagroup.graphql.generator.directives

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.annotations.GraphQLDeprecated
import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.getTestSchemaConfigWithHooks
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.generator.testSchemaConfig
import com.expediagroup.graphql.generator.toSchema
import graphql.Scalars
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class DirectiveTests {

    @Test
    fun `SchemaGenerator marks deprecated fields in the return objects`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithDeprecatedFields())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("Query")
        val query = topLevelQuery.getFieldDefinition("deprecatedFieldQuery")
        val result = (query.type as? GraphQLNonNull)?.wrappedType as? GraphQLObjectType
        val deprecatedField = result?.getFieldDefinition("deprecatedField")
        val graphqlDeprecatedField = result?.getFieldDefinition("graphqlDeprecatedField")

        assertEquals(deprecatedField?.isDeprecated, true)
        assertEquals(graphqlDeprecatedField?.isDeprecated, true)
        assertEquals("this field is deprecated", deprecatedField?.deprecationReason)
        assertEquals("this field is also deprecated", graphqlDeprecatedField?.deprecationReason)
    }

    @Test
    fun `SchemaGenerator marks deprecated queries and documents replacement`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithDeprecatedFields())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("Query")
        val deprecatedQueryWithReplacement = topLevelQuery.getFieldDefinition("deprecatedQueryWithReplacement")
        val graphqlDeprecatedQueryWithReplacement = topLevelQuery.getFieldDefinition("graphqlDeprecatedQueryWithReplacement")

        assertTrue(deprecatedQueryWithReplacement.isDeprecated)
        assertTrue(graphqlDeprecatedQueryWithReplacement.isDeprecated)
        assertEquals("this query is also deprecated, replace with shinyNewQuery", deprecatedQueryWithReplacement.deprecationReason)
        assertEquals("this query is also deprecated, replace with shinyNewQuery", graphqlDeprecatedQueryWithReplacement.deprecationReason)
    }

    @Test
    fun `SchemaGenerator marks deprecated queries`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithDeprecatedFields())), config = testSchemaConfig)
        val topLevelQuery = schema.getObjectType("Query")
        val query = topLevelQuery.getFieldDefinition("deprecatedQuery")
        val graphqlDeprecatedQuery = topLevelQuery.getFieldDefinition("graphqlDeprecatedQuery")

        assertTrue(query.isDeprecated)
        assertTrue(graphqlDeprecatedQuery.isDeprecated)
        assertEquals("this query is deprecated", query.deprecationReason)
        assertEquals("this query is also deprecated", graphqlDeprecatedQuery.deprecationReason)
    }

    @Test
    fun `Default directive names are normalized`() {
        val wiring = object : KotlinSchemaDirectiveWiring {}
        val config = getTestSchemaConfigWithHooks(
            object : SchemaGeneratorHooks {
                override val wiringFactory: KotlinDirectiveWiringFactory
                    get() = KotlinDirectiveWiringFactory(manualWiring = mapOf("dummyDirective" to wiring, "RightNameDirective" to wiring))
            }
        )
        val schema = toSchema(queries = listOf(TopLevelObject(QueryObject())), config = config)

        val query = schema.queryType.getFieldDefinition("query")
        assertNotNull(query)
        assertNotNull(query.getAppliedDirective("dummyDirective"))
    }

    @Test
    fun `Custom directive names are not modified`() {
        val wiring = object : KotlinSchemaDirectiveWiring {}
        val config = getTestSchemaConfigWithHooks(
            object : SchemaGeneratorHooks {
                override val wiringFactory: KotlinDirectiveWiringFactory
                    get() = KotlinDirectiveWiringFactory(manualWiring = mapOf("dummyDirective" to wiring, "RightNameDirective" to wiring))
            }
        )
        val schema = toSchema(queries = listOf(TopLevelObject(QueryObject())), config = config)

        val possibleDirective = (schema.getType("Location") as? GraphQLObjectType)?.getAppliedDirective("RightNameDirective")
        val directive = assertNotNull(possibleDirective)

        assertEquals("arenaming", directive.arguments[0].argumentValue.value)
        assertEquals("arg", directive.arguments[0].name)
        assertTrue(GraphQLNonNull(Scalars.GraphQLString).isEqualTo(directive.arguments[0].type))
    }
}

@GraphQLDirective(name = "RightNameDirective")
annotation class WrongNameDirective(val arg: String)

@GraphQLDirective
annotation class DummyDirective

class Geography(
    val id: Int?,
    val type: GeoType,
    val locations: List<Location>
) {
    @Suppress("Detekt.FunctionOnlyReturningConstant")
    fun somethingCool(): String = "Something cool"
}

enum class GeoType {
    CITY, STATE
}

@WrongNameDirective(arg = "arenaming")
data class Location(val lat: Double, val lon: Double)

class QueryObject {

    @DummyDirective
    fun query(value: Int): Geography = Geography(value, GeoType.CITY, listOf())
}

@Suppress("unused")
class QueryWithDeprecatedFields {
    @Deprecated("this query is deprecated")
    fun deprecatedQuery(something: String) = something

    @Deprecated("this query is also deprecated", replaceWith = ReplaceWith("shinyNewQuery"))
    fun deprecatedQueryWithReplacement(something: String) = something

    fun deprecatedFieldQuery(something: String) = ClassWithDeprecatedField(something, something.reversed())

    fun deprecatedArgumentQuery(input: ClassWithDeprecatedField) = input.something

    @GraphQLDeprecated("this query is also deprecated")
    fun graphqlDeprecatedQuery(something: String) = something

    @Deprecated("this query is also deprecated", replaceWith = ReplaceWith("shinyNewQuery"))
    fun graphqlDeprecatedQueryWithReplacement(something: String) = something
}

data class ClassWithDeprecatedField(
    val something: String,
    @Deprecated("this field is deprecated")
    val deprecatedField: String,
    @GraphQLDeprecated("this field is also deprecated")
    val graphqlDeprecatedField: String = ""
)
