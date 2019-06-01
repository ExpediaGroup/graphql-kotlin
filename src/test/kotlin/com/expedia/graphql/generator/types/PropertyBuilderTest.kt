package com.expedia.graphql.generator.types

import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.annotations.GraphQLID
import com.expedia.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.hooks.NoopSchemaGeneratorHooks
import graphql.Scalars
import graphql.introspection.Introspection
import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactory
import graphql.schema.GraphQLNonNull
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@Suppress("Detekt.UnusedPrivateClass")
internal class PropertyBuilderTest : TypeTestHelper() {

    @GraphQLDirective(locations = [Introspection.DirectiveLocation.FIELD])
    internal annotation class PropertyDirective(val arg: String)

    private class ClassWithProperties {
        @GraphQLDescription("The truth")
        @Deprecated("It's not a lie")
        @PropertyDirective("trust me")
        lateinit var cake: String

        @Deprecated("Healthy food is deprecated", replaceWith = ReplaceWith("cake"))
        lateinit var healthyFood: String

        var nullableCake: String? = null
    }

    private data class DataClassWithProperties(
        @GraphQLDescription("A great description")
        val fooBar: String,

        @GraphQLID
        val myId: String
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
        val prop = ClassWithProperties::cake
        val result = builder.property(prop, ClassWithProperties::class)

        assertTrue(result.isDeprecated)
        assertEquals("It's not a lie", result.deprecationReason)
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

        assertEquals("The truth", result.description)
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
            setOf(Introspection.DirectiveLocation.FIELD)
        )
    }

    @Test
    fun `Test nullable property`() {
        val prop = ClassWithProperties::nullableCake
        val result = builder.property(prop, ClassWithProperties::class)

        assertNull(result.description)
        assertTrue(result.type !is GraphQLNonNull)
//        assertTrue(result.dataFetcher is PropertyDataFetcher)
    }

    @Test
    fun `Test lateinit, non-null property with datafetcher factory`() {
        val localConfig: SchemaGeneratorConfig = mockk()
        every { localConfig.hooks } returns NoopSchemaGeneratorHooks()
        every { localConfig.supportedPackages } returns emptyList()
        val mockDataFetcherFactoryProvider: KotlinDataFetcherFactoryProvider = mockk()
        val mockFactory: DataFetcherFactory<Any> = mockk()
        val mockDataFetcher: DataFetcher<Any> = mockk()
        every { mockDataFetcherFactoryProvider.propertyDataFetcherFactory(any(), any()) } returns mockFactory
        every { mockFactory.get(any()) } returns mockDataFetcher
        every { localConfig.dataFetcherFactoryProvider } returns mockDataFetcherFactoryProvider
        val localGenerator = SchemaGenerator(localConfig)
        val localBuilder = PropertyBuilder(localGenerator)

        val prop = ClassWithProperties::cake
        val result = localBuilder.property(prop, ClassWithProperties::class)

//        assertFalse(result.dataFetcher is PropertyDataFetcher)
//        assertEquals(expected = mockDataFetcher, actual = result.dataFetcher)
    }
}
