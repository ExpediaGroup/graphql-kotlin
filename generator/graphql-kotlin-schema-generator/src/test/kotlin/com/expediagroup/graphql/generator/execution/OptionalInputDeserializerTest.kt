package com.expediagroup.graphql.client.jackson.serializers

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.convertValue
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Test
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OptionalInputDeserializerTest {
    private val mapper = jacksonObjectMapper()

    @Test
    fun `verify null value is deserialized correctly`() {
        val iw: InputWrapper = mapper.convertValue(
            mapOf(
                "optionalInput" to null,
                "optionalInputList" to listOf(
                    "b4770220-4f1a-4f28-8757-a3d12c4288ff",
                ),
            )
        )
        assertIs<OptionalInput.Defined<Int>>(iw.optionalInput)
        assertNull(iw.optionalInput.value)
    }

    @Test
    fun `verify undefined value is deserialized correctly`() {
        val iw: InputWrapper = mapper.convertValue(
            mapOf(
                "optionalInputList" to listOf(
                    "b4770220-4f1a-4f28-8757-a3d12c4288ff",
                ),
                "innerInput" to mapOf<String, String>()
            )
        )
        assertIs<OptionalInput.Undefined>(iw.optionalInput)
        val innerInput = assertNotNull(iw.innerInput)
        assertIs<OptionalInput.Undefined>(innerInput.optionalInput)
    }

    @Test
    fun `verify defined values are deserialized with correct type`() {
        val iw: InputWrapper = mapper.convertValue(
            mapOf(
                "optionalInputList" to listOf(
                    "b4770220-4f1a-4f28-8757-a3d12c4288ff",
                ),
            )
        )
        assertIs<OptionalInput.Defined<List<UUID>>>(iw.optionalInputList)
        val value = assertNotNull(iw.optionalInputList.value)
        assertEquals(UUID.fromString("b4770220-4f1a-4f28-8757-a3d12c4288ff"), value.first())
    }


    data class InputWrapper(
        val optionalInput: OptionalInput<Int>,
        val optionalInputList: OptionalInput<List<UUID>> = OptionalInput.Undefined,
        val innerInput: InnerInput?,
    )

    data class InnerInput(
        val optionalInput: OptionalInput<Int>,
    )
}
