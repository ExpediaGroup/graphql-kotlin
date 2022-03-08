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

package com.expediagroup.graphql.examples.server.spring.query

import com.expediagroup.graphql.examples.server.spring.model.Selection
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component
import java.util.Random

/**
 * Example queries.
 */
@Component
class SimpleQuery : Query {

    @Deprecated(message = "this query is deprecated", replaceWith = ReplaceWith("shinyNewQuery"))
    @GraphQLDescription("old query that should not be used always returns false")
    fun simpleDeprecatedQuery(): Boolean = false

    @GraphQLDescription("new query that always returns true")
    fun shinyNewQuery(): Boolean = true

    @GraphQLIgnore
    fun notPartOfSchema() = "ignore me!"

    private fun privateFunctionsAreNotVisible() = "ignored private function"

    @GraphQLDescription("performs some operation")
    fun doSomething(
        @GraphQLDescription("super important value")
        value: Int
    ): Boolean = true

    @GraphQLDescription("generates pseudo random int and returns it if it is less than 50")
    fun generateNullableNumber(): Int? {
        val num = Random().nextInt(100)
        return if (num < 50) num else null
    }

    @GraphQLDescription("generates pseudo random int")
    fun generateNumber(): Int = Random().nextInt(100)

    @GraphQLDescription("generates pseudo random list of ints")
    fun generateList(): List<Int> {
        val random = Random()
        return (1..10).map { random.nextInt(100) }.toList()
    }

    @GraphQLDescription("query with optional input")
    fun doSomethingWithOptionalInput(
        @GraphQLDescription("this field is required") requiredValue: Int,
        @GraphQLDescription("this field is optional") optionalValue: Int? = null
    ) =
        "required value=$requiredValue, optional value=$optionalValue"

    @GraphQLDescription("Demonstrate the usage of custom enum input")
    fun getAValue(selection: Selection): String = when (selection) {
        Selection.ONE -> "You chose the first one"
        Selection.TWO -> "You chose the second one"
    }

    @GraphQLDescription("Set exposed as a function")
    fun setList(): Set<Int> = setOf(1, 1, 2, 3)
}
