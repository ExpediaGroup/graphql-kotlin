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

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveWiring
import com.expediagroup.graphql.generator.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.execution.PropertyDataFetcher
import com.expediagroup.graphql.generator.extensions.deepName
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.generator.internal.extensions.getSimpleName
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.generator.test.utils.SimpleDirective
import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactory
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLTypeUtil
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Test
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateClass")
class GeneratePropertyTest : TypeTestHelper() {

    @GraphQLDirective(locations = [Introspection.DirectiveLocation.FIELD_DEFINITION])
    internal annotation class PropertyDirective(val arg: String)

    class ClassWithProperties {
        @GraphQLDescription("It's not a lie")
        @PropertyDirective("trust me")
        val cake: String = "chocolate"

        @Deprecated("Only cake")
        lateinit var dessert: String

        @Deprecated("Healthy food is deprecated", replaceWith = ReplaceWith("cake"))
        lateinit var healthyFood: String

        var nullableCake: String? = null

        @GraphQLName("pie")
        val renameMe: String = "apple"

        val setList: Set<Int> = setOf(1, 1, 2, 3)
    }

    private data class DataClassWithProperties(
        @GraphQLDescription("A great description")
        val fooBar: String,

        val myId: ID,

        @SimpleDirective
        val directiveWithNoPrefix: String,

        @property:SimpleDirective
        val directiveWithPrefix: String
    )

    @Test
    fun `Test naming`() {
        val prop = ClassWithProperties::cake
        val result = generateProperty(generator, prop, ClassWithProperties::class)

        assertEquals("cake", result.name)
        val registry = generator.codeRegistry.build()
        val coordinates = FieldCoordinates.coordinates("ClassWithProperties", "cake")
        assertNotNull(registry.getDataFetcher(coordinates, result))
    }

    @Test
    fun `Test naming override`() {
        val prop = ClassWithProperties::renameMe
        val result = generateProperty(generator, prop, ClassWithProperties::class)

        assertEquals("pie", result.name)
        val registry = generator.codeRegistry.build()
        val coordinates = FieldCoordinates.coordinates("ClassWithProperties", "pie")
        assertNotNull(registry.getDataFetcher(coordinates, result))
    }

    @Test
    fun `Test deprecation`() {
        @Suppress("DEPRECATION")
        val prop = ClassWithProperties::dessert
        val result = generateProperty(generator, prop, ClassWithProperties::class)

        assertTrue(result.isDeprecated)
        assertEquals("Only cake", result.deprecationReason)
    }

    @Test
    fun `Test deprecation with replacement`() {
        @Suppress("DEPRECATION")
        val prop = ClassWithProperties::healthyFood
        val result = generateProperty(generator, prop, ClassWithProperties::class)

        assertTrue(result.isDeprecated)
        assertEquals("Healthy food is deprecated, replace with cake", result.deprecationReason)
    }

    @Test
    fun `Test description`() {
        val prop = ClassWithProperties::cake
        val result = generateProperty(generator, prop, ClassWithProperties::class)

        assertEquals("It's not a lie", result.description)
    }

    @Test
    fun `Test description on data class`() {
        val prop = DataClassWithProperties::fooBar
        val result = generateProperty(generator, prop, DataClassWithProperties::class)

        assertEquals("A great description", result.description)
    }

    @Test
    fun `Test graphql id on data class`() {
        val prop = DataClassWithProperties::myId
        val result = generateProperty(generator, prop, DataClassWithProperties::class)

        assertEquals("ID", (GraphQLTypeUtil.unwrapNonNull(result.type) as? GraphQLNamedType)?.name)
    }

    @Test
    fun `Test custom directive`() {
        val prop = ClassWithProperties::cake
        val result = generateProperty(generator, prop, ClassWithProperties::class)

        assertEquals(1, result.appliedDirectives.size)
        val appliedDirective = result.appliedDirectives[0]
        assertEquals("propertyDirective", appliedDirective.name)
        assertEquals("trust me", appliedDirective.arguments[0].argumentValue.value)
        assertEquals("arg", appliedDirective.arguments[0].name)
        assertTrue(GraphQLNonNull(Scalars.GraphQLString).isEqualTo(appliedDirective.arguments[0].type))

        val schemaDirective = generator.directives[appliedDirective.name]
        assertEquals(
            schemaDirective?.validLocations()?.toSet(),
            setOf(Introspection.DirectiveLocation.FIELD_DEFINITION)
        )
    }

    @Test
    fun `Properties with no directives are not set`() {
        val resultWithPrefix = generateProperty(generator, DataClassWithProperties::myId, DataClassWithProperties::class)
        assertEquals(0, resultWithPrefix.directives.size)
    }

    @Test
    fun `Properties can have directives on the constructor args`() {
        val resultWithPrefix = generateProperty(generator, DataClassWithProperties::directiveWithPrefix, DataClassWithProperties::class)
        assertEquals(1, resultWithPrefix.appliedDirectives.size)
        assertEquals("simpleDirective", resultWithPrefix.appliedDirectives.first().name)

        val resultWithNoPrefix = generateProperty(generator, DataClassWithProperties::directiveWithNoPrefix, DataClassWithProperties::class)
        assertEquals(1, resultWithNoPrefix.appliedDirectives.size)
        assertEquals("simpleDirective", resultWithNoPrefix.appliedDirectives.first().name)
    }

    @Test
    fun `Test nullable property`() {
        val prop = ClassWithProperties::nullableCake
        val result = generateProperty(generator, prop, ClassWithProperties::class)

        assertNull(result.description)
        assertTrue(result.type !is GraphQLNonNull)

        val parentType = ClassWithProperties::class.getSimpleName()
        val coordinates = FieldCoordinates.coordinates(parentType, prop.name)
        val targetDataFetcher = generator.codeRegistry.getDataFetcher(coordinates, result)
        assertTrue(targetDataFetcher is PropertyDataFetcher)
    }

    @Test
    fun `Test lateinit, non-null property with datafetcher factory`() {
        val hooks: SchemaGeneratorHooks = object : SchemaGeneratorHooks {
            override val wiringFactory: KotlinDirectiveWiringFactory
                get() = spyk(KotlinDirectiveWiringFactory()) {
                    every { getSchemaDirectiveWiring(any()) } returns object : KotlinSchemaDirectiveWiring {}
                }
        }
        val mockDataFetcher: DataFetcher<Any?> = mockk()
        val mockFactory: DataFetcherFactory<Any?> = mockk()
        val mockDataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider = mockk()

        every { mockDataFetcherFactoryProvider.propertyDataFetcherFactory(any(), any()) } returns mockFactory
        every { mockFactory.get(any()) } returns mockDataFetcher

        val localConfig = SchemaGeneratorConfig(
            supportedPackages = emptyList(),
            hooks = hooks,
            dataFetcherFactoryProvider = mockDataFetcherFactoryProvider
        )
        val localGenerator = SchemaGenerator(localConfig)

        val prop = ClassWithProperties::cake
        val result = generateProperty(localGenerator, prop, ClassWithProperties::class)

        val parentType = ClassWithProperties::class.getSimpleName()
        val coordinates = FieldCoordinates.coordinates(parentType, prop.name)
        val targetDataFetcher = localGenerator.codeRegistry.getDataFetcher(coordinates, result)
        assertFalse(targetDataFetcher is PropertyDataFetcher)
        assertEquals(expected = mockDataFetcher, actual = targetDataFetcher)
        localGenerator.close()
    }

    @Test
    fun `hooks are called on properties`() {
        val hooks: SchemaGeneratorHooks = object : SchemaGeneratorHooks {
            override fun willResolveMonad(type: KType): KType = when (type.classifier) {
                Set::class -> List::class.createType(type.arguments)
                else -> type
            }
        }

        val localConfig = SchemaGeneratorConfig(
            supportedPackages = emptyList(),
            hooks = hooks
        )
        val localGenerator = SchemaGenerator(localConfig)

        val prop = ClassWithProperties::setList
        val result = generateProperty(localGenerator, prop, ClassWithProperties::class)

        assertEquals("[Int!]!", result.type.deepName)
    }
}
