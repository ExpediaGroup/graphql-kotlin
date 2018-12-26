package com.expedia.graphql

import com.expedia.graphql.exceptions.InvalidSchemaException
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

internal class ToSchemaKtTest {

    internal class TestClass

    @Test
    fun `valid schema`() {
        val queries = listOf(TopLevelObjectDef(TestClass()))
        toSchema(queries = queries, config = testSchemaConfig)
    }

    @Test
    fun `empty queries and mutations`() {
        val mutations = listOf(TopLevelObjectDef(TestClass()))
        assertFailsWith(InvalidSchemaException::class) {
            toSchema(queries = emptyList(), mutations = mutations, config = testSchemaConfig)
        }
    }

    @Test
    fun `empty queries with mutations`() {
        assertFailsWith(InvalidSchemaException::class) {
            toSchema(queries = emptyList(), config = testSchemaConfig)
        }
    }
}
