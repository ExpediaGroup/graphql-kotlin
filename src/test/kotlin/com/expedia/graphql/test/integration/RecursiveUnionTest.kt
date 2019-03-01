package com.expedia.graphql.test.integration

import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.testSchemaConfig
import com.expedia.graphql.toSchema
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RecursiveUnionTest {

    @Test
    fun recursiveUnion() {
        val queries = listOf(TopLevelObject(RecursiveUnionQuery()))
        val schema = toSchema(queries = queries, config = testSchemaConfig)
        assertEquals(1, schema.queryType.fieldDefinitions.size)
        val field = schema.queryType.fieldDefinitions.first()
        assertEquals("getRoot", field.name)
    }
}

class RecursiveUnionQuery {
    fun getRoot() = RecursiveUnionA()
}

interface SomeUnion

class RecursiveUnionA : SomeUnion {
    fun getB() = RecursiveUnionB()
}

class RecursiveUnionB : SomeUnion {
    fun getA() = RecursiveUnionA()
}
