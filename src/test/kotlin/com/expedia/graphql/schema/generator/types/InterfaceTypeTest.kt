package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import graphql.schema.GraphQLInterfaceType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

@Suppress("Detekt.UnsafeCast")
internal class InterfaceTypeTest : TypeTestHelper() {

    private lateinit var builder: InterfaceTypeBuilder

    override fun beforeTest() {
        builder = InterfaceTypeBuilder(generator)
    }

    @GraphQLDescription("The truth")
    private interface HappyInterface

    @Test
    fun `Test naming`() {
        val result = builder.interfaceType(HappyInterface::class) as GraphQLInterfaceType
        assertEquals("HappyInterface", result.name)
    }

    @Test
    fun `Test description`() {
        val result = builder.interfaceType(HappyInterface::class) as GraphQLInterfaceType
        assertEquals("The truth", result.description)
    }
}
