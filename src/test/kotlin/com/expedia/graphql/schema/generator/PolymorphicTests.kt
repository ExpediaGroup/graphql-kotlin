package com.expedia.graphql.schema.generator

import com.expedia.graphql.TopLevelObjectDef
import com.expedia.graphql.exceptions.InvalidInputFieldTypeException
import com.expedia.graphql.schema.testSchemaConfig
import com.expedia.graphql.toSchema
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class PolymorphicTests {

    @Test
    fun `Schema generator creates union types from marked up interface`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithUnion())), config = testSchemaConfig)

        val graphqlType = schema.getType("BodyPart") as? GraphQLUnionType
        assertNotNull(graphqlType)
        assertTrue(graphqlType.types.any { it.name == "LeftHand" })
        assertTrue(graphqlType.types.any { it.name == "RightHand" })
        assertTrue(graphqlType.types.any { it.name == "Arm" })

        assertNotNull(schema.getType("RightHand"))
        val leftHandType = schema.getType("LeftHand") as? GraphQLObjectType

        assertNotNull(leftHandType)
        assertNotNull(leftHandType.getFieldDefinition("associatedWith"))
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

    @Test
    fun `Object types implementing union and interfaces are only created once`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithInterfaceAnUnion())), config = testSchemaConfig)

        val carType = schema.getType("Car") as? GraphQLObjectType
        assertNotNull(carType)
        assertNotNull(carType.getFieldDefinition("model"))
        assertNotNull(carType.getFieldDefinition("color"))

        val productType = schema.getType("Product") as? GraphQLUnionType
        assertNotNull(productType)
        assertTrue(productType.types.contains(carType))
    }

    @Test
    fun `Interfaces can declare properties of their own type`() {
        val schema = toSchema(listOf(TopLevelObjectDef(QueryWithRecursiveType())), config = testSchemaConfig)

        val personType = schema.getType("Person")
        assertNotNull(personType)
    }
}

class QueryWithInterface {
    fun fromUnion(): Union = AnImplementation()
    fun query(): AnInterface = AnImplementation()
    fun fromImplementation(): AnImplementation = AnImplementation()
}

class QueryWithUnAuthorizedInterfaceArgument {
    fun notAllowed(arg: AnInterface): AnInterface = arg
}

class QueryWithUnAuthorizedUnionArgument {
    fun notAllowed(body: BodyPart): BodyPart = body
}

interface Union

interface AnInterface {
    val property: String
}

data class AnImplementation(
    override val property: String = "A value",
    val implementationSpecific: String = "It's implementation specific"
) : AnInterface, Union

class QueryWithUnion {
    fun query(whichHand: String): BodyPart = when (whichHand) {
        "right" -> RightHand(12)
        else -> LeftHand("hello world", Arm(true))
    }
}

interface BodyPart

data class LeftHand(
    val field: String,
    val associatedWith: BodyPart
) : BodyPart

data class RightHand(
    val property: Int
) : BodyPart

data class Arm(
    val value: Boolean
) : BodyPart

class QueryWithInterfaceAnUnion {
    fun product(): Product = Car("DB9", "black")
}

interface Vehicle {
    val color: String
}

interface Product

data class Car(val model: String, override val color: String) : Vehicle, Product

class QueryWithRecursiveType {
    fun query(): Person = Father("knock knock", Child())
}

interface Person {
    val child: Person?
}

data class Child(
    override val child: Person? = null
) : Person

data class Father(
    val dadJoke: String,
    override val child: Person?
) : Person
