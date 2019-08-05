package com.expedia.graphql.hooks

import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.generator.extensions.getSimpleName
import com.expedia.graphql.getTestSchemaConfigWithHooks
import com.expedia.graphql.toSchema
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SchemaGeneratorHooksTest {

    @Test
    fun `calls hook before schema is built`() {
        class MockSchemaGeneratorHooks : SchemaGeneratorHooks {
            var willBuildSchemaCalled = false
            override fun willBuildSchema(builder: GraphQLSchema.Builder): GraphQLSchema.Builder {
                willBuildSchemaCalled = true
                builder.additionalTypes(setOf(GraphQLObjectType.newObject().name("InjectedFromHook").build()))
                return builder
            }
        }

        val hooks = MockSchemaGeneratorHooks()
        val schema = toSchema(
            queries = listOf(TopLevelObject(TestQuery())),
            config = getTestSchemaConfigWithHooks(hooks)
        )
        assertTrue(hooks.willBuildSchemaCalled)
        assertNotNull(schema.getType("InjectedFromHook"))
    }

    @Test
    fun `calls hook to filter property`() {
        class MockSchemaGeneratorHooks : SchemaGeneratorHooks {
            var calledFilterFunction = false

            override fun isValidProperty(property: KProperty<*>): Boolean {
                calledFilterFunction = true
                return false
            }
        }

        val hooks = MockSchemaGeneratorHooks()
        val schema = toSchema(
            queries = listOf(TopLevelObject(TestQuery())),
            config = getTestSchemaConfigWithHooks(hooks)
        )
        assertTrue(hooks.calledFilterFunction)
        assertFalse(schema.queryType.fieldDefinitions.isEmpty())
        assertTrue(schema.getObjectType("SomeData").fieldDefinitions.isEmpty())
    }

    @Test
    fun `calls hook to filter functions`() {
        class MockSchemaGeneratorHooks : SchemaGeneratorHooks {
            var calledFilterFunction = false

            override fun isValidFunction(function: KFunction<*>): Boolean {
                calledFilterFunction = true
                return false
            }
        }

        val hooks = MockSchemaGeneratorHooks()
        val schema = toSchema(
            queries = listOf(TopLevelObject(TestQuery())),
            config = getTestSchemaConfigWithHooks(hooks)
        )
        assertTrue(hooks.calledFilterFunction)
        assertTrue(schema.queryType.fieldDefinitions.isEmpty())
    }

    @Test
    fun `calls hook after generating object type`() {
        class MockSchemaGeneratorHooks : SchemaGeneratorHooks {
            val seenTypes = mutableSetOf<KType>()
            override fun didGenerateGraphQLType(type: KType, generatedType: GraphQLType): GraphQLType {
                seenTypes.add(type)
                return generatedType
            }
        }

        val hooks = MockSchemaGeneratorHooks()
        toSchema(
            queries = listOf(TopLevelObject(TestQuery())),
            config = getTestSchemaConfigWithHooks(hooks)
        )
        assertTrue(hooks.seenTypes.contains(RandomData::class.createType()))
        assertTrue(hooks.seenTypes.contains(SomeData::class.createType()))
        // TODO bug: didGenerateGraphQLType hook is not applied on the object types build from the interface
//        assertTrue(hooks.seenTypes.contains(OtherData::class.createType()))
    }

    @Test
    fun `calls hook before adding type to schema`() {
        class MockSchemaGeneratorHooks : SchemaGeneratorHooks {
            var hookCalled = false

            override fun willAddGraphQLTypeToSchema(type: KType, generatedType: GraphQLType): GraphQLType {
                hookCalled = true
                return when {
                    generatedType is GraphQLObjectType && generatedType.name == "SomeData" -> GraphQLObjectType.newObject(generatedType).description("My custom description").build()
                    generatedType is GraphQLInterfaceType -> GraphQLInterfaceType.newInterface(generatedType).description("My custom interface description").build()
                    else -> generatedType
                }
            }
        }

        val hooks = MockSchemaGeneratorHooks()
        val schema = toSchema(
            queries = listOf(TopLevelObject(TestQuery())),
            config = getTestSchemaConfigWithHooks(hooks)
        )
        assertTrue(hooks.hookCalled)

        val type = schema.getObjectType("SomeData")
        assertNotNull(type)
        assertEquals(expected = "My custom description", actual = type.description)
    }

    @Test
    fun `calls hook before adding query to schema`() {
        class MockSchemaGeneratorHooks : SchemaGeneratorHooks {
            override fun didGenerateQueryType(
                function: KFunction<*>,
                fieldDefinition: GraphQLFieldDefinition
            ): GraphQLFieldDefinition {
                val newField = GraphQLFieldDefinition.Builder()
                newField.description("Hijacked Description")
                newField.name(fieldDefinition.name)
                newField.type(fieldDefinition.type)
                newField.arguments(fieldDefinition.arguments)
                return newField.build()
            }
        }

        val hooks = MockSchemaGeneratorHooks()
        val schema = toSchema(
            queries = listOf(TopLevelObject(TestQuery())),
            config = getTestSchemaConfigWithHooks(hooks)
        )
        val topLevelQuery = schema.getObjectType("Query")
        val query = topLevelQuery.getFieldDefinition("query")
        assertEquals("Hijacked Description", query.description)
    }

    @Test
    fun `calls hook before adding mutation to schema`() {
        class MockSchemaGeneratorHooks : SchemaGeneratorHooks {
            override fun didGenerateMutationType(
                function: KFunction<*>,
                fieldDefinition: GraphQLFieldDefinition
            ): GraphQLFieldDefinition {
                val newField = GraphQLFieldDefinition.Builder()
                newField.description("Hijacked Description")
                newField.name(fieldDefinition.name)
                newField.type(fieldDefinition.type)
                newField.arguments(fieldDefinition.arguments)
                return newField.build()
            }
        }

        val hooks = MockSchemaGeneratorHooks()
        val schema = toSchema(
            queries = listOf(TopLevelObject(TestQuery())),
            mutations = listOf(TopLevelObject(TestQuery())),
            config = getTestSchemaConfigWithHooks(hooks)
        )
        val topLevelQuery = schema.getObjectType("Mutation")
        val query = topLevelQuery.getFieldDefinition("query")
        assertEquals("Hijacked Description", query.description)
    }

    @Test
    fun `willResolveMonad returns basic type`() {
        val hooks = NoopSchemaGeneratorHooks()
        val type = TestQuery::query.returnType

        assertEquals(expected = "SomeData", actual = hooks.willResolveMonad(type).getSimpleName())
    }

    class TestQuery {
        fun query(): SomeData = SomeData("someData", 0)
        fun randomQuery(): RandomData = if (Random.nextBoolean()) {
            SomeData("random", 1)
        } else {
            OtherData("random", 1)
        }
    }

    interface RandomData {
        val id: String
    }

    data class SomeData(override val id: String, val someNumber: Int) : RandomData

    data class OtherData(override val id: String, val otherNumber: Int) : RandomData
}
