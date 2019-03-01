package com.expedia.graphql.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.test.utils.SimpleDirective
import graphql.schema.GraphQLInterfaceType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class InterfaceTypeBuilderTest : TypeTestHelper() {

    private lateinit var builder: InterfaceTypeBuilder

    override fun beforeTest() {
        builder = InterfaceTypeBuilder(generator)
    }

    @Suppress("Detekt.UnusedPrivateClass")
    @GraphQLDescription("The truth")
    @SimpleDirective
    private interface HappyInterface

    @Test
    fun `Test naming`() {
        val result = builder.interfaceType(HappyInterface::class) as? GraphQLInterfaceType
        assertEquals("HappyInterface", result?.name)
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
}
