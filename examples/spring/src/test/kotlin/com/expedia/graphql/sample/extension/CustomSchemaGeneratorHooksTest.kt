package com.expedia.graphql.sample.extension

import com.expedia.graphql.directives.KotlinDirectiveWiringFactory
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.util.UUID
import javax.validation.Validator
import kotlin.reflect.full.createType
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CustomSchemaGeneratorHooksTest {

    data class NonScalar(val id: String)

    @Test
    fun `UUID returns a string scalar`() {
        val validator: Validator = mockk()
        val wiringFactory: KotlinDirectiveWiringFactory = mockk()
        val hooks = CustomSchemaGeneratorHooks(validator, wiringFactory)

        val result = hooks.willGenerateGraphQLType(UUID::class.createType())
        assertNotNull(result)
        assertEquals(expected = "UUID", actual = result.name)
    }

    @Test
    fun `Non valid type returns null`() {
        val validator: Validator = mockk()
        val wiringFactory: KotlinDirectiveWiringFactory = mockk()
        val hooks = CustomSchemaGeneratorHooks(validator, wiringFactory)

        val result = hooks.willGenerateGraphQLType(NonScalar::class.createType())
        assertNull(result)
    }
}
