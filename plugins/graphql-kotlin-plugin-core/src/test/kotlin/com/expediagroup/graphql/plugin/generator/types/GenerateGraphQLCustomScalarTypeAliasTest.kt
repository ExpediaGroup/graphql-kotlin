package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.mockContext
import graphql.language.Description
import graphql.language.ScalarTypeDefinition
import graphql.language.SourceLocation
import io.mockk.mockk
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GenerateGraphQLCustomScalarTypeAliasTest {

    @Test
    fun `verify can generate type alias for custom scalar`() {
        val expected = """
            /**
             * custom UUID scalar
             */
            typealias UUID = kotlin.String""".trimIndent()
        val customScalarDefinition = ScalarTypeDefinition.newScalarTypeDefinition()
            .name("UUID")
            .description(Description("custom UUID scalar", SourceLocation(0, 0), false))
            .build()
        val typeAlias = generateGraphQLCustomScalarTypeAlias(mockContext(), customScalarDefinition)
        assertEquals(expected, typeAlias.toString().trim())
    }

}
