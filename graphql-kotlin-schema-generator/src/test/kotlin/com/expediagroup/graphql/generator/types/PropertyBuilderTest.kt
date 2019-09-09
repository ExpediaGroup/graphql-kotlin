/*
 * Copyright 2019 Expedia Group, Inc.
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

package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.annotations.GraphQLDirective
import com.expediagroup.graphql.annotations.GraphQLID
import com.expediagroup.graphql.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.directives.KotlinSchemaDirectiveWiring
import com.expediagroup.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.extensions.getSimpleName
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.test.utils.SimpleDirective
import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactory
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLNonNull
import graphql.schema.PropertyDataFetcher
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateClass")
internal class PropertyBuilderTest : TypeTestHelper() {

    @GraphQLDirective(locations = [Introspection.DirectiveLocation.FIELD_DEFINITION])
    internal annotation class PropertyDirective(val arg: String)

    private class ClassWithProperties {
        @GraphQLDescription("It's not a lie")
        @PropertyDirective("trust me")
        lateinit var cake: String

        @Deprecated("Only cake")
        lateinit var dessert: String

        @Deprecated("Healthy food is deprecated", replaceWith = ReplaceWith("cake"))
        lateinit var healthyFood: String

        var nullableCake: String? = null
    }

    private data class DataClassWithProperties(
        @GraphQLDescription("A great description")
        val fooBar: String,

        @GraphQLID
        val myId: String,

        @SimpleDirective
        val directiveWithNoPrefix: String,

        @property:SimpleDirective
        val directiveWithPrefix: String
    )

    private lateinit var builder: PropertyBuilder

    override fun beforeTest() {
        builder = PropertyBuilder(generator)
    }

    @Test
    fun `Test naming`() {
        val prop = ClassWithProperties::cake
        val result = builder.property(prop, ClassWithProperties::class)

        assertEquals("cake", result.name)
    }

    @Test
    fun `Test deprecation`() {
        val prop = ClassWithProperties::dessert
        val result = builder.property(prop, ClassWithProperties::class)

        assertTrue(result.isDeprecated)
        assertEquals("Only cake", result.deprecationReason)
    }

    @Test
    fun `Test deprecation with replacement`() {
        val prop = ClassWithProperties::healthyFood
        val result = builder.property(prop, ClassWithProperties::class)

        assertTrue(result.isDeprecated)
        assertEquals("Healthy food is deprecated, replace with cake", result.deprecationReason)
    }

    @Test
    fun `Test description`() {
        val prop = ClassWithProperties::cake
        val result = builder.property(prop, ClassWithProperties::class)

        assertEquals("It's not a lie", result.description)
    }

    @Test
    fun `Test description on data class`() {
        val prop = DataClassWithProperties::fooBar
        val result = builder.property(prop, DataClassWithProperties::class)

        assertEquals("A great description", result.description)
    }

    @Test
    fun `Test graphql id on data class`() {
        val prop = DataClassWithProperties::myId
        val result = builder.property(prop, DataClassWithProperties::class)

        assertEquals("ID", (result.type as? GraphQLNonNull)?.wrappedType?.name)
    }

    @Test
    fun `Test custom directive`() {
        val prop = ClassWithProperties::cake
        val result = builder.property(prop, ClassWithProperties::class)

        assertEquals(1, result.directives.size)
        val directive = result.directives[0]
        assertEquals("propertyDirective", directive.name)
        assertEquals("trust me", directive.arguments[0].value)
        assertEquals("arg", directive.arguments[0].name)
        assertEquals(GraphQLNonNull(Scalars.GraphQLString), directive.arguments[0].type)
        assertEquals(
            directive.validLocations()?.toSet(),
            setOf(Introspection.DirectiveLocation.FIELD_DEFINITION)
        )
    }

    @Test
    fun `Properties with no directives are not set`() {
        val resultWithPrefix = builder.property(DataClassWithProperties::myId, DataClassWithProperties::class)
        assertEquals(0, resultWithPrefix.directives.size)
    }

    @Test
    fun `Properties can have directives on the constructor args`() {
        val resultWithPrefix = builder.property(DataClassWithProperties::directiveWithPrefix, DataClassWithProperties::class)
        assertEquals(1, resultWithPrefix.directives.size)
        assertEquals("simpleDirective", resultWithPrefix.directives.first().name)

        val resultWithNoPrefix = builder.property(DataClassWithProperties::directiveWithNoPrefix, DataClassWithProperties::class)
        assertEquals(1, resultWithNoPrefix.directives.size)
        assertEquals("simpleDirective", resultWithNoPrefix.directives.first().name)
    }

    @Test
    fun `Test nullable property`() {
        val prop = ClassWithProperties::nullableCake
        val result = builder.property(prop, ClassWithProperties::class)

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
        val mockDataFetcher: DataFetcher<Any> = mockk()
        val mockFactory: DataFetcherFactory<Any> = mockk()
        val mockDataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider = mockk()

        every { mockDataFetcherFactoryProvider.propertyDataFetcherFactory(any(), any()) } returns mockFactory
        every { mockFactory.get(any()) } returns mockDataFetcher

        val localConfig = SchemaGeneratorConfig(
            supportedPackages = emptyList(),
            hooks = hooks,
            dataFetcherFactoryProvider = mockDataFetcherFactoryProvider
        )
        val localGenerator = SchemaGenerator(localConfig)
        val localBuilder = PropertyBuilder(localGenerator)

        val prop = ClassWithProperties::cake
        val result = localBuilder.property(prop, ClassWithProperties::class)

        val parentType = ClassWithProperties::class.getSimpleName()
        val coordinates = FieldCoordinates.coordinates(parentType, prop.name)
        val targetDataFetcher = localGenerator.codeRegistry.getDataFetcher(coordinates, result)
        assertFalse(targetDataFetcher is PropertyDataFetcher)
        assertEquals(expected = mockDataFetcher, actual = targetDataFetcher)
    }
}
