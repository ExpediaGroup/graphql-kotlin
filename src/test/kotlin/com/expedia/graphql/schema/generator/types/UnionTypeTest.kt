package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class UnionTypeTest : TypeTestHelper() {

    private lateinit var builder: UnionTypeBuilder

    override fun beforeTest() {
        mockTypeCache()
        mockSubTypeMapper()
        mockObjectTypeBuilder()

        builder = UnionTypeBuilder(generator)
    }

    @GraphQLDescription("The truth")
    private interface Cake

    @GraphQLDescription("so red")
    private class StrawBerryCake : Cake

    @GraphQLDescription("so yellow")
    private class BananaCake : Cake

    @Test
    fun `Test description`() {
        val result = builder.unionType(Cake::class) as? GraphQLUnionType
        assertNotNull(result)
        assertEquals("The truth", result.description)
        assertEquals(2, result.types.size)
    }
}
