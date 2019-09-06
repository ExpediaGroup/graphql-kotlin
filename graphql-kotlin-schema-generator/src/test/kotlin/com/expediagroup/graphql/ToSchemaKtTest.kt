package com.expediagroup.graphql

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ToSchemaKtTest {

    @Suppress("FunctionOnlyReturningConstant")
    class TestClass {
        fun greeting() = "Hello World"
    }

    @Test
    fun `valid schema`() {
        val queries = listOf(TopLevelObject(TestClass()))
        toSchema(queries = queries, config = testSchemaConfig)
    }

    @Test
    fun `generate standalone SDL`() {
        val queries = listOf(TopLevelObject(TestClass::class))
        val schema = toSchema(queries = queries, config = testSchemaConfig)

        val greetingField = schema.queryType.getFieldDefinition("greeting")
        assertEquals("String!", greetingField.type.toString())
    }
}
