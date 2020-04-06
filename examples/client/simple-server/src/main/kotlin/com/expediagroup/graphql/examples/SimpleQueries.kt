/*
 * Copyright 2020 Expedia, Inc
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
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.random.Random

@Component
class SimpleQueries: Query {
    private val random = Random

    @GraphQLDescription("Query that returns enum value")
    fun enumQuery() = CustomEnum.values().random()

    @GraphQLDescription("Query that returns wrapper object with all supported scalar types")
    fun scalarQuery() = ScalarWrapper(
        id = random.nextInt().toString(),
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
        children = emptyList()
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
}
