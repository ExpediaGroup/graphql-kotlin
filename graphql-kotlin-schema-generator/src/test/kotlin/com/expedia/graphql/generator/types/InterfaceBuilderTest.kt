package com.expedia.graphql.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLName
import com.expedia.graphql.test.utils.SimpleDirective
import graphql.schema.GraphQLInterfaceType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class InterfaceBuilderTest : TypeTestHelper() {

    private lateinit var builder: InterfaceBuilder

    override fun beforeTest() {
        builder = InterfaceBuilder(generator)
    }

    @Suppress("Detekt.UnusedPrivateClass")
    @GraphQLDescription("The truth")
    @SimpleDirective
    private interface HappyInterface

    @Suppress("Detekt.UnusedPrivateClass")
    @GraphQLName("HappyInterfaceRenamed")
    private interface HappyInterfaceCustomName

    @Test
    fun `Test naming`() {
        val result = builder.interfaceType(HappyInterface::class) as? GraphQLInterfaceType
        assertEquals("HappyInterface", result?.name)
    }

    @Test
    fun `Test custom naming`() {
        val result = builder.interfaceType(HappyInterfaceCustomName::class) as? GraphQLInterfaceType
        assertEquals("HappyInterfaceRenamed", result?.name)
    }

    @Test
    fun `Test description`() {
        val result = builder.interfaceType(HappyInterface::class) as? GraphQLInterfaceType
        assertEquals("The truth", result?.description)
    }

    @Test
    fun `Interfaces can have directives`() {
        val result = builder.interfaceType(HappyInterface::class) as? GraphQLInterfaceType
        assertEquals(1, result?.directives?.size)
        assertEquals("simpleDirective", result?.directives?.first()?.name)
    }

    @Test
    fun `verify interface is build only once`() {
        val cache = generator.state.cache
        assertTrue(cache.doesNotContain(HappyInterface::class))

        val first = builder.interfaceType(HappyInterface::class) as? GraphQLInterfaceType
        assertNotNull(first)
        assertFalse(cache.doesNotContain(HappyInterface::class))
        val second = builder.interfaceType(HappyInterface::class) as? GraphQLInterfaceType
        assertNotNull(second)
        assertEquals(first.hashCode(), second.hashCode())
    }
}
