package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@Suppress("Detekt.UnsafeCast")
internal class UnionTypeTest : TypeTestHelper() {

    private lateinit var builder: UnionTypeBuilder

    override fun beforeTest() {
        builder = UnionTypeBuilder(generator)
    }

    @GraphQLDescription("The truth")
    private interface Cake

    @GraphQLDescription("so red")
    private class StrawBerryCake : Cake

    @Test
    fun `Test naming`() {
        val result = builder.unionType(Cake::class) as? GraphQLUnionType
        assertNotNull(result)

        assertEquals("Cake", result.name)
        assertEquals(1, result.types.size)
        assertEquals("StrawBerryCake", result.types[0].name)
    }

    @Test
    fun `Test description`() {
        val result = builder.unionType(Cake::class) as? GraphQLUnionType
        assertNotNull(result)

        assertEquals("The truth", result.description)
        assertEquals(1, result.types.size)
        assertEquals("so red", (result.types[0] as GraphQLObjectType).description)
    }
}
