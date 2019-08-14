package com.expedia.graphql.test.integration

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
    fun getRoot() = RecursiveClassA()
}

class InterfaceWithSelfFieldQuery {
    fun getInterface() = InterfaceWithSelfFieldB()
}

interface SomeInterfaceWithId {
    val id: String
}

interface InterfaceWithSelfField {
    val parent: InterfaceWithSelfField?
}

class RecursiveClassA : SomeInterfaceWithId {
    override val id = "A"
    fun getB() = RecursiveClassB()
}

class RecursiveClassB : SomeInterfaceWithId {
    override val id = "B"
    fun getA() = RecursiveClassA()
}

class InterfaceWithSelfFieldA : InterfaceWithSelfField {
    override val parent: InterfaceWithSelfField? = null
}

class InterfaceWithSelfFieldB : InterfaceWithSelfField {
    override val parent = InterfaceWithSelfFieldA()
}
