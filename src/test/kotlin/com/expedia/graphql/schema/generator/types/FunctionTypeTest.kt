package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.schema.extensions.getValidFunctions
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class FunctionTypeTest : TypeTestHelper() {

    private lateinit var builder: FunctionTypeBuilder

    override fun beforeTest() {
        mockTypeCache()
        mockSubTypeMapper()
        mockScalarTypeBuilder()

        builder = FunctionTypeBuilder(generator)
    }

    private class Happy {

        @GraphQLDescription("By bob")
        @Deprecated("No more little trees >:|")
        fun littleTrees() = UUID.randomUUID().toString()
    }

    @Test
    fun `Test description`() {
        val kFunction = Happy::class.getValidFunctions(hooks)[0]
        val result = builder.function(kFunction)
        assertEquals("By bob\n\nDirectives: deprecated", result.description)
    }

    @Test
    fun `Test deprecation`() {
        val kFunction = Happy::class.getValidFunctions(hooks)[0]
        val result = builder.function(kFunction)
        assertTrue(result.isDeprecated)
        assertEquals("No more little trees >:|", result.deprecationReason)
    }
}
