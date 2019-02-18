package com.expedia.graphql.integration

import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.testSchemaConfig
import com.expedia.graphql.toSchema
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RecursiveInterfaceTest {

    @Test
    fun recursiveInterface() {
        val queries = listOf(TopLevelObject(RecursiveInterfaceQuery()))
        val schema = toSchema(queries = queries, config = testSchemaConfig)
        assertEquals(1, schema.queryType.fieldDefinitions.size)
        val field = schema.queryType.fieldDefinitions.first()
        assertEquals("getRoot", field.name)
    }

    @Test
    fun `interface with self field`() {
        val queries = listOf(TopLevelObject(InterfaceWithSelfFieldQuery()))
        val schema = toSchema(queries = queries, config = testSchemaConfig)
        assertEquals(1, schema.queryType.fieldDefinitions.size)
        val field = schema.queryType.fieldDefinitions.first()
        assertEquals("getInterface", field.name)
    }
}

class RecursiveInterfaceQuery {
    fun getRoot() = RecursiveInterfaceA()
}

class InterfaceWithSelfFieldQuery {
    fun getInterface() = InterfaceWithSelfFieldB()
}

interface SomeInterface {
    val id: String
}

interface InterfaceWithSelfField {
    val parent: InterfaceWithSelfField?
}

class RecursiveInterfaceA : SomeInterface {
    override val id = "A"
    fun getB() = RecursiveInterfaceB()
}

class RecursiveInterfaceB : SomeInterface {
    override val id = "B"
    fun getA() = RecursiveInterfaceA()
}

class InterfaceWithSelfFieldA : InterfaceWithSelfField {
    override val parent: InterfaceWithSelfField? = null
}

class InterfaceWithSelfFieldB : InterfaceWithSelfField {
    override val parent = InterfaceWithSelfFieldA()
}
