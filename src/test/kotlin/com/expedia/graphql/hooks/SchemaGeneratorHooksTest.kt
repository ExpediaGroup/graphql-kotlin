package com.expedia.graphql.hooks

import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.extensions.deepName
import com.expedia.graphql.generator.extensions.getSimpleName
import com.expedia.graphql.getTestSchemaConfigWithHooks
import com.expedia.graphql.toSchema
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.test.Test
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
            listOf(TopLevelObject(TestQuery())),
            config = getTestSchemaConfigWithHooks(hooks)
        )
        assertTrue(hooks.willBuildSchemaCalled)
        assertNotNull(schema.getType("InjectedFromHook"))
    }

    @Test
    fun `calls hook before generating object type`() {
        class MockSchemaGeneratorHooks : SchemaGeneratorHooks {
            var willGenerateGraphQLTypeCalled = false
            override fun willGenerateGraphQLType(type: KType): GraphQLType? {
                willGenerateGraphQLTypeCalled = true
                return GraphQLObjectType.newObject().name("InterceptedFromHook").build()
            }
        }

        val hooks = MockSchemaGeneratorHooks()
        val schema = toSchema(
            listOf(TopLevelObject(TestQuery())),
            config = getTestSchemaConfigWithHooks(hooks)
        )
        val topLevelQuery = schema.getObjectType("TopLevelQuery")
        val query = topLevelQuery.getFieldDefinition("query")
        assertTrue(hooks.willGenerateGraphQLTypeCalled)
        assertEquals("InterceptedFromHook!", query.type.deepName)
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
            listOf(TopLevelObject(TestQuery())),
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
            listOf(TopLevelObject(TestQuery())),
            config = getTestSchemaConfigWithHooks(hooks)
        )
        assertTrue(hooks.calledFilterFunction)
        assertTrue(schema.queryType.fieldDefinitions.isEmpty())
    }

    @Test
    fun `calls hook after generating object type`() {
        class MockSchemaGeneratorHooks : SchemaGeneratorHooks {
            var lastSeenType: KType? = null
            var lastSeenGeneratedType: GraphQLType? = null
            override fun didGenerateGraphQLType(type: KType, generatedType: GraphQLType) {
                lastSeenType = type
                lastSeenGeneratedType = generatedType
            }
        }

        val hooks = MockSchemaGeneratorHooks()
        toSchema(
            listOf(TopLevelObject(TestQuery())),
            config = getTestSchemaConfigWithHooks(hooks)
        )
        assertEquals(SomeData::class.createType(), hooks.lastSeenType)
        assertEquals("SomeData!", hooks.lastSeenGeneratedType?.deepName)
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
                newField.argument(fieldDefinition.arguments)
                return newField.build()
            }
        }

        val hooks = MockSchemaGeneratorHooks()
        val schema = toSchema(
            listOf(TopLevelObject(TestQuery())),
            config = getTestSchemaConfigWithHooks(hooks)
        )
        val topLevelQuery = schema.getObjectType("TopLevelQuery")
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
                newField.argument(fieldDefinition.arguments)
                return newField.build()
            }
        }

        val hooks = MockSchemaGeneratorHooks()
        val schema = toSchema(
            queries = listOf(TopLevelObject(TestQuery())),
            mutations = listOf(TopLevelObject(TestQuery())),
            config = getTestSchemaConfigWithHooks(hooks)
        )
        val topLevelQuery = schema.getObjectType("TopLevelMutation")
        val query = topLevelQuery.getFieldDefinition("query")
        assertEquals("Hijacked Description", query.description)
    }

    @Test
    fun `willResolveMonad returns CompletableFuture wrapped type`() {
        val hooks = NoopSchemaGeneratorHooks()
        val type = TestQueryFuture::comepletableFutre.returnType

        assertEquals(expected = "SomeData", actual = hooks.willResolveMonad(type).getSimpleName())
    }

    @Test
    fun `willResolveMonad returns basic type`() {
        val hooks = NoopSchemaGeneratorHooks()
        val type = TestQuery::query.returnType

        assertEquals(expected = "SomeData", actual = hooks.willResolveMonad(type).getSimpleName())
    }

    class TestQuery {
        fun query(): SomeData = SomeData(0)
    }

    class TestQueryFuture {
        fun comepletableFutre(): CompletableFuture<SomeData> = CompletableFuture.completedFuture(SomeData(1))
    }

    data class SomeData(val someNumber: Int)
}
