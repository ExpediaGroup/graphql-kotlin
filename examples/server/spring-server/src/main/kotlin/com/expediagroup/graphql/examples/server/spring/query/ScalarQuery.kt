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

package com.expediagroup.graphql.examples.server.spring.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.types.operations.Mutation
import com.expediagroup.graphql.types.operations.Query
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.UUID
import kotlin.random.Random

/**
 * Simple query that exposes custom scalar.
 */
@Component
class ScalarQuery : Query {

    @GraphQLDescription("generates random UUID")
    fun generateRandomUUID() = UUID.randomUUID()

    @GraphQLDescription("Prints a string with a custom scalar as input")
    fun printUuids(uuids: List<UUID>) = "You sent $uuids"

    fun findPersonById(id: ID) = Person(id, "Nelson")

    @GraphQLDescription("generates random GraphQL ID")
    fun generateRandomId() = ID(UUID.randomUUID().toString())

    @GraphQLDescription("generates random BigDecimal")
    fun generateRandomBigDecimal(): BigDecimal = BigDecimal(Random.nextLong())
}

@Component
class ScalarMutation : Mutation {
    fun addPerson(person: Person): Person = person
}

data class Person(
    val id: ID,
    val name: String
)
