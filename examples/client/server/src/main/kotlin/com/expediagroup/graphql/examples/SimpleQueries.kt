package com.expediagroup.graphql.examples

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.examples.model.BasicInterface
import com.expediagroup.graphql.examples.model.BasicObject
import com.expediagroup.graphql.examples.model.BasicUnion
import com.expediagroup.graphql.examples.model.ComplexObject
import com.expediagroup.graphql.examples.model.CustomEnum
import com.expediagroup.graphql.examples.model.DetailsObject
import com.expediagroup.graphql.examples.model.FirstInterfaceImplementation
import com.expediagroup.graphql.examples.model.NestedObject
import com.expediagroup.graphql.examples.model.ScalarWrapper
import com.expediagroup.graphql.examples.model.SecondInterfaceImplementation
import com.expediagroup.graphql.examples.model.SimpleArgument
import com.expediagroup.graphql.examples.repository.BasicObjectRepository
import com.expediagroup.graphql.spring.operations.Query
import com.expediagroup.graphql.types.ID
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.random.Random

@Component
class SimpleQueries(private val repository: BasicObjectRepository) : Query {
    private val random = Random

    @GraphQLDescription("Basic `HelloWorld` Query")
    fun helloWorld(@GraphQLDescription("optional name, defaults to `World` if not specified") name: String?) =
            "Hello ${name ?: "World"}"

    @GraphQLDescription("Query that returns enum value")
    fun enumQuery() = CustomEnum.values().random()

    @GraphQLDescription("Query that returns wrapper object with all supported scalar types")
    fun scalarQuery() = ScalarWrapper(
        id = ID(random.nextInt().toString()),
        name = "Scalar Wrapper",
        valid = true,
        count = 1,
        rating = null,
        custom = UUID.randomUUID()
    )

    @GraphQLDescription("Query returning list of simple objects")
    fun listQuery(): List<BasicObject> = listOf(BasicObject(id = random.nextInt(), name = "whatever"))

    @GraphQLDescription("Query returning an object that references another object")
    fun complexObjectQuery(): ComplexObject = ComplexObject(
        id = random.nextInt(),
        name = "whatever",
        details = DetailsObject(
            id = random.nextInt(),
            flag = random.nextBoolean(),
            value = "details"
        )
    )

    @GraphQLDescription("Query returning object referencing itself")
    fun nestedObjectQuery(): NestedObject = NestedObject(
        id = random.nextInt(),
        name = "nested",
        children = listOf(
            NestedObject(id = random.nextInt(), name = "child1", children = emptyList()),
            NestedObject(id = random.nextInt(), name = "child2", children = listOf(
                NestedObject(id = random.nextInt(), name = "grandkid", children = emptyList())
            ))
        )
    )

    @GraphQLDescription("Query returning an interface")
    fun interfaceQuery(): BasicInterface = if (random.nextBoolean()) {
        FirstInterfaceImplementation(
            id = random.nextInt(),
            name = "first",
            intValue = random.nextInt()
        )
    } else {
        SecondInterfaceImplementation(
            id = random.nextInt(),
            name = "second",
            floatValue = random.nextFloat()
        )
    }

    @GraphQLDescription("Query returning union")
    fun unionQuery(): BasicUnion = if (random.nextBoolean()) {
        BasicObject(
            id = random.nextInt(),
            name = "basic"
        )
    } else {
        ComplexObject(
            id = random.nextInt(),
            name = "complex",
            details = DetailsObject(
                id = random.nextInt(),
                flag = random.nextBoolean(),
                value = "details"
            )
        )
    }

    @GraphQLDescription("Query that accepts some input arguments")
    fun inputObjectQuery(criteria: SimpleArgument): Boolean = random.nextBoolean()

    @Deprecated(message = "old query should not be used")
    @GraphQLDescription("Deprecated query that should not be used anymore")
    fun deprecatedQuery(): String = "this query is deprecated, random: ${random.nextInt()}"

    @GraphQLDescription("Retrieve simple object from the repository")
    fun retrieveBasicObject(id: Int): BasicObject? = repository.get(id)
}
