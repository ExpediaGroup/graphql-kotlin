package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import graphql.schema.GraphQLObjectType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class ObjectTypeTest : TypeTestHelper() {

    private lateinit var builder: ObjectTypeBuilder

    override fun beforeTest() {
        mockTypeCache()
        mockSubTypeMapper()

        builder = ObjectTypeBuilder(generator)
    }

    @GraphQLDescription("The truth")
    private interface HappyInterface

    @Test
    fun `Test description`() {
        val result = builder.objectType(HappyInterface::class) as? GraphQLObjectType
        assertNotNull(result)
        assertEquals("The truth", result.description)
    }
}
