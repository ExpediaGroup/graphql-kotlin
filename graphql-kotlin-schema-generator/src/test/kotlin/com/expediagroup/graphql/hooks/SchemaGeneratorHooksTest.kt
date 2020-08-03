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

package com.expediagroup.graphql.hooks

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.annotations.GraphQLIgnore
import com.expediagroup.graphql.exceptions.EmptyInputObjectTypeException
import com.expediagroup.graphql.exceptions.EmptyInterfaceTypeException
import com.expediagroup.graphql.exceptions.EmptyObjectTypeException
import com.expediagroup.graphql.extensions.deepName
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.extensions.getSimpleName
import com.expediagroup.graphql.getTestSchemaConfigWithHooks
import com.expediagroup.graphql.test.utils.graphqlUUIDType
import com.expediagroup.graphql.testSchemaConfig
import com.expediagroup.graphql.toSchema
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactive.asPublisher
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.reactivestreams.Publisher
import java.util.UUID
import kotlin.random.Random
import kotlin.reflect.KClass
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

            override fun isValidProperty(kClass: KClass<*>, property: KProperty<*>): Boolean {
                calledFilterFunction = true
                return false
            }

            // skip validation
            override fun didGenerateGraphQLType(type: KType, generatedType: GraphQLType): GraphQLType = generatedType
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

            override fun isValidFunction(kClass: KClass<*>, function: KFunction<*>): Boolean {
                calledFilterFunction = true
                return false
            }

            // skip validation
            override fun didGenerateGraphQLType(type: KType, generatedType: GraphQLType): GraphQLType = generatedType

            // skip validation
            override fun didGenerateQueryObject(type: GraphQLObjectType): GraphQLObjectType = type
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
    fun `calls hook to filter additionalTypes`() {
        class MockSchemaGeneratorHooks : SchemaGeneratorHooks {
            var calledFilterFunction = false

            override fun isValidAdditionalType(kClass: KClass<*>, inputType: Boolean): Boolean {
                calledFilterFunction = true
                return true
            }
        }

        class CustomGenerator(config: SchemaGeneratorConfig) : SchemaGenerator(config) {
            fun addTypesWithAnnotation(annotation: KClass<*>) = super.addAdditionalTypesWithAnnotation(annotation, false)
            fun getAdditionalTypesCount() = additionalTypes.size
        }

        val hooks = MockSchemaGeneratorHooks()
        val generator = CustomGenerator(getTestSchemaConfigWithHooks(hooks))

        assertFalse(hooks.calledFilterFunction)
        assertEquals(0, generator.getAdditionalTypesCount())

        generator.addTypesWithAnnotation(CustomAnnotation::class)

        assertTrue(hooks.calledFilterFunction)
        assertEquals(1, generator.getAdditionalTypesCount())
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
            queries = listOf(TopLevelObject(TestInterfaceQuery())),
            config = getTestSchemaConfigWithHooks(hooks)
        )
        assertTrue(hooks.seenTypes.contains(RandomData::class.createType()))
        assertTrue(hooks.seenTypes.contains(SomeData::class.createType()))
        assertTrue(hooks.seenTypes.contains(OtherData::class.createType()))
    }

    @Test
    fun `empty object type will not be added to the schema`() {
        assertThrows<EmptyObjectTypeException> {
            toSchema(
                queries = listOf(TopLevelObject(TestWithEmptyObjectQuery())),
                config = testSchemaConfig
            )
        }
    }

    @Test
    fun `empty input object type will not be added to the schema`() {
        assertThrows<EmptyInputObjectTypeException> {
            toSchema(
                queries = listOf(TopLevelObject(TestWithEmptyInputObjectQuery())),
                config = testSchemaConfig
            )
        }
    }

    @Test
    fun `empty interface will not be added to the schema`() {
        assertThrows<EmptyInterfaceTypeException> {
            toSchema(
                queries = listOf(TopLevelObject(TestWithEmptyInterfaceQuery())),
                config = testSchemaConfig
            )
        }
    }

    @Test
    fun `calls hook before adding type to schema`() {
        class MockSchemaGeneratorHooks : SchemaGeneratorHooks {
            var hookCalled = false

            override fun willAddGraphQLTypeToSchema(type: KType, generatedType: GraphQLType): GraphQLType {
                hookCalled = true
                return when {
                    generatedType is GraphQLObjectType && generatedType.name == "SomeData" -> GraphQLObjectType.newObject(generatedType).description("My custom description").build()
                    generatedType is GraphQLInterfaceType && generatedType.name == "RandomData" ->
                        GraphQLInterfaceType.newInterface(generatedType).description("My custom interface description").build()
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

        val interfaceType = schema.getType("RandomData") as? GraphQLInterfaceType
        assertNotNull(interfaceType)
        assertEquals(expected = "My custom interface description", actual = interfaceType.description)
    }

    @Test
    fun `calls hook before adding query field to schema`() {
        class MockSchemaGeneratorHooks : SchemaGeneratorHooks {
            override fun didGenerateQueryField(
                kClass: KClass<*>,
                function: KFunction<*>,
                fieldDefinition: GraphQLFieldDefinition
            ): GraphQLFieldDefinition {
                val newField = GraphQLFieldDefinition.Builder(fieldDefinition)
                newField.description("Hijacked Description")
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
    fun `calls hook before adding mutation field to schema`() {
        class MockSchemaGeneratorHooks : SchemaGeneratorHooks {
            override fun didGenerateMutationField(
                kClass: KClass<*>,
                function: KFunction<*>,
                fieldDefinition: GraphQLFieldDefinition
            ): GraphQLFieldDefinition {
                val newField = GraphQLFieldDefinition.Builder(fieldDefinition)
                newField.description("Hijacked Description")
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
    fun `noop subscription basics`() {
        for (hooks in listOf(NoopSchemaGeneratorHooks, FlowSubscriptionSchemaGeneratorHooks())) {
            val schema = toSchema(
                queries = listOf(TopLevelObject(TestQuery())),
                mutations = listOf(TopLevelObject(TestQuery())),
                subscriptions = listOf(TopLevelObject(TestSubscription())),
                config = getTestSchemaConfigWithHooks(hooks)
            )
            assertTrue(hooks.isValidSubscriptionReturnType(Publisher::class, TestSubscription::subscription))
            val topLevelSub = schema.getObjectType("Subscription")
            val sub = topLevelSub.getFieldDefinition("subscription")
            assertEquals("SomeData!", sub.type.deepName)
        }
    }

    @Test
    fun `willResolveMonad returns basic type`() {
        val hooks = NoopSchemaGeneratorHooks
        val type = TestQuery::query.returnType

        assertEquals(expected = "SomeData", actual = hooks.willResolveMonad(type).getSimpleName())
    }

    @Test
    fun `willGenerateGraphQLType can override to provide a custom type`() {
        class MockSchemaGeneratorHooks : SchemaGeneratorHooks {
            var hookCalled = false

            override fun willGenerateGraphQLType(type: KType): GraphQLType? {
                hookCalled = true

                return when (type.classifier as? KClass<*>) {
                    UUID::class -> graphqlUUIDType
                    else -> null
                }
            }
        }

        val hooks = MockSchemaGeneratorHooks()
        val schema = toSchema(
            queries = listOf(TopLevelObject(CustomTypesQuery())),
            config = getTestSchemaConfigWithHooks(hooks)
        )

        assertTrue(hooks.hookCalled)
        val graphQLType = assertNotNull(schema.getType("UUID"))
        assertTrue(graphQLType is GraphQLScalarType)
    }

    class TestQuery {
        fun query(): SomeData = SomeData("someData", 0)
    }

    class TestSubscription {
        fun subscription(): Publisher<SomeData> = flowOf(SomeData("someData", 0)).asPublisher()
    }

    class CustomTypesQuery {
        fun uuid(): UUID = UUID.randomUUID()
    }

    class TestInterfaceQuery {
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

    class TestWithEmptyObjectQuery {
        fun emptyObject(): EmptyData = EmptyData()
    }

    class EmptyData

    class TestWithEmptyInputObjectQuery {
        fun emptyObject(input: PrivateData) = input
    }

    @Suppress("Detekt.UnusedPrivateMember")
    data class PrivateData(private val id: String)

    class TestWithEmptyInterfaceQuery {
        fun emptyInterface(): EmptyInterface = EmptyImplementation("123")
    }

    annotation class CustomAnnotation

    @CustomAnnotation
    interface EmptyInterface {
        @GraphQLIgnore
        val id: String
    }

    class EmptyImplementation(override val id: String) : EmptyInterface
}
