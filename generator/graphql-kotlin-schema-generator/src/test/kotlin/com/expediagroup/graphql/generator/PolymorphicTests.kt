/*
 * Copyright 2022 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.generator

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.exceptions.InvalidInputFieldTypeException
import graphql.TypeResolutionEnvironment
import graphql.execution.TypeResolutionParameters
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PolymorphicTests {

    @Test
    fun `Schema generator creates union types from marked up interface`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithUnion())), config = testSchemaConfig)

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
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithInterface())), config = testSchemaConfig)

        val interfaceType = schema.getType("AnInterface") as? GraphQLInterfaceType
        assertNotNull(interfaceType)

        val implementationType = schema.getObjectType("AnImplementation")
        assertNotNull(implementationType)
        assertEquals(1, implementationType.interfaces.size)
        assertEquals(implementationType.interfaces.first(), interfaceType)
    }

    @Test
    fun `Interfaces cannot be used as input field types`() {
        assertThrows(InvalidInputFieldTypeException::class.java) {
            toSchema(queries = listOf(TopLevelObject(QueryWithUnAuthorizedInterfaceArgument())), config = testSchemaConfig)
        }
    }

    @Test
    fun `Union cannot be used as input field types`() {
        assertThrows(InvalidInputFieldTypeException::class.java) {
            toSchema(queries = listOf(TopLevelObject(QueryWithUnAuthorizedUnionArgument())), config = testSchemaConfig)
        }
    }

    @Test
    fun `Object types implementing union and interfaces are only created once`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithInterfaceAndUnion())), config = testSchemaConfig)

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
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithRecursiveType())), config = testSchemaConfig)

        val personType = schema.getType("Person")
        assertNotNull(personType)
    }

    @Test
    fun `Abstract classes should be converted to interfaces`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithAbstract())), config = testSchemaConfig)

        val abstractInterface = schema.getType("MyAbstract") as? GraphQLInterfaceType
        assertNotNull(abstractInterface)

        val classWithBaseAbstractType = schema.getObjectType("MyClass")
        assertNotNull(classWithBaseAbstractType)
        assertEquals(1, classWithBaseAbstractType.interfaces.size)
        assertEquals(classWithBaseAbstractType.interfaces.first(), abstractInterface)
    }

    @Test
    fun `Interface types can be correctly resolved`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithRenamedAbstracts())), config = testSchemaConfig)

        val cakeInterface = schema.getType("Cake") as? GraphQLInterfaceType
        assertNotNull(cakeInterface)
        val cakeResolver = schema.codeRegistry.getTypeResolver(cakeInterface)
        val cheesecakeResolver = cakeResolver.getType(mockTypeResolutionEnvironment(Cheesecake(), schema))
        assertNotNull(cheesecakeResolver)
        assertEquals("Cheesecake", cheesecakeResolver.name)

        val strawberryCakeResolver = cakeResolver.getType(mockTypeResolutionEnvironment(BerryCake(), schema))
        assertNotNull(strawberryCakeResolver)
        assertEquals("StrawberryCake", strawberryCakeResolver.name)
    }

    @Test
    fun `Union types can be correctly resolved`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithRenamedAbstracts())), config = testSchemaConfig)

        val dessertUnion = schema.getType("Dessert") as? GraphQLUnionType
        assertNotNull(dessertUnion)
        val dessertResolver = schema.codeRegistry.getTypeResolver(dessertUnion)
        val iceCreamResolver = dessertResolver.getType(mockTypeResolutionEnvironment(IceCream(), schema))
        assertNotNull(iceCreamResolver)
        assertEquals("IceCream", iceCreamResolver.name)

        val strawberryCakeResolver = dessertResolver.getType(mockTypeResolutionEnvironment(BerryCake(), schema))
        assertNotNull(strawberryCakeResolver)
        assertEquals("StrawberryCake", strawberryCakeResolver.name)
    }

    @Test
    fun `Interface implementations are not computed when marked with GraphQLIgnore annotation`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithIgnoredInfo())), config = testSchemaConfig)
        val service = schema.getType("Service") as? GraphQLInterfaceType
        assertNotNull(service)

        val webService = schema.getType("WebService") as? GraphQLObjectType
        assertNotNull(webService)

        val microService = schema.getType("MicroService") as? GraphQLObjectType
        assertNull(microService)
    }

    @Test
    fun `Ignored interface properties should not appear in the subtype`() {
        val schema = toSchema(queries = listOf(TopLevelObject(QueryWithIgnoredInfo())), config = testSchemaConfig)
        val service = schema.getType("Service") as? GraphQLInterfaceType
        assertNotNull(service)
        val interfaceIgnoredField = service.getFieldDefinition("shouldNotBeInTheSchema")
        assertNull(interfaceIgnoredField)

        val webService = schema.getType("WebService") as? GraphQLObjectType
        assertNotNull(webService)
        val subtypeIgnoredField = webService.getFieldDefinition("shouldNotBeInTheSchema")
        assertNull(subtypeIgnoredField)
    }

    private fun mockTypeResolutionEnvironment(target: Any, schema: GraphQLSchema): TypeResolutionEnvironment =
        TypeResolutionParameters.newParameters()
            .value(target)
            .schema(schema)
            .build()
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

class QueryWithInterfaceAndUnion {
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

class QueryWithAbstract {
    fun query(): MyAbstract = MyClass(id = 1, name = "JUnit")
}

@Suppress("UnnecessaryAbstractClass")
abstract class MyAbstract {
    abstract val id: Int
}

data class MyClass(override val id: Int, val name: String) : MyAbstract()

class QueryWithRenamedAbstracts {

    fun randomCake(): Cake = if (Random.nextBoolean()) {
        BerryCake()
    } else {
        Cheesecake()
    }

    fun randomDessert(): Dessert = if (Random.nextBoolean()) {
        IceCream()
    } else {
        BerryCake()
    }
}

interface Cake {
    fun recipe(): String
}

@GraphQLName("StrawberryCake")
class BerryCake : Cake, Dessert {
    override fun recipe(): String = "google it"
}

class Cheesecake : Cake {
    override fun recipe(): String = "use bing"
}

interface Dessert

@Suppress("Detekt.FunctionOnlyReturningConstant")
class IceCream : Dessert {
    fun flavor(): String = "chocolate"
}

class QueryWithIgnoredInfo {
    fun webservice(): Service = WebService("gql-kotlin-service")
    fun microservice(): Service = MicroService("micro-gql-kotlin-service")
}

interface Service {
    val name: String

    @GraphQLIgnore
    val shouldNotBeInTheSchema: Boolean
}

data class WebService(override val name: String, override val shouldNotBeInTheSchema: Boolean = false) : Service

@GraphQLIgnore
data class MicroService(override val name: String, override val shouldNotBeInTheSchema: Boolean = true) : Service
