package com.expedia.graphql.schema.generator

import com.expedia.graphql.TopLevelObjectDef
import com.expedia.graphql.schema.exceptions.InvalidInputFieldTypeException
import com.expedia.graphql.schema.testSchemaConfig
import com.expedia.graphql.toSchema
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class PolymorphicTests {

    @Test
    fun `Schema generator creates union types from marked up interface`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithUnion())), config = testSchemaConfig)

        val graphqlType = schema.getType("BodyPart") as? GraphQLUnionType
        assertNotNull(graphqlType)
        graphqlType?.let { union ->
            assertTrue(union.types.any { it.name == "LeftHand" })
            assertTrue(union.types.any { it.name == "RightHand" })
        }

        assertNotNull(schema.getType("RightHand"))
        assertNotNull(schema.getType("LeftHand"))
    }

    @Test
    fun `SchemaGenerator can expose an interface and its implementations`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithInterface())), config = testSchemaConfig)

        val interfaceType = schema.getType("AnInterface")
        assertNotNull(interfaceType)

        val implementationType = schema.getObjectType("AnImplementation")
        assertNotNull(implementationType)
        assertEquals(1, implementationType.interfaces.size)
        assertEquals(implementationType.interfaces.first(), interfaceType)
    }

    @Test
    fun `Interfaces cannot be used as input field types`() {
        assertThrows(InvalidInputFieldTypeException::class.java) {
            toSchema(listOf(TopLevelObjectDef(QueryWithUnAuthorizedInterfaceArgument())), config = testSchemaConfig)
        }
    }

    @Test
    fun `Union cannot be used as input field types`() {
        assertThrows(InvalidInputFieldTypeException::class.java) {
            toSchema(listOf(TopLevelObjectDef(QueryWithUnAuthorizedUnionArgument())), config = testSchemaConfig)
        }
    }
}

class QueryWithInterface {
    fun query(): AnInterface = AnImplementation()
    fun fromImplementation(): AnImplementation = AnImplementation()
}

class QueryWithUnAuthorizedInterfaceArgument {
    fun notAllowed(arg: AnInterface): AnInterface = arg
}

class QueryWithUnAuthorizedUnionArgument {
    fun notAllowed(body: BodyPart): BodyPart = body
}

interface AnInterface {
    val property: String
}

data class AnImplementation(
    override val property: String = "A value",
    val implementationSpecific: String = "It's implementation specific"
) : AnInterface

class QueryWithUnion {
    fun query(whichHand: String): BodyPart = when (whichHand) {
        "right" -> RightHand(12)
        else -> LeftHand("hello world")
    }
}

interface BodyPart

data class LeftHand(
    val field: String
) : BodyPart

data class RightHand(
    val property: Int
) : BodyPart
