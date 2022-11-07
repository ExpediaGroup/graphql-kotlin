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

package com.expediagroup.graphql.examples.federation.reviews

import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.scalars.ID

private val reviews: Map<ID, List<Review>> = mapOf(
    ID("2") to listOf(Review(ID("1020"), "Very cramped :( Do not recommend.", 2), Review(ID("1021"), "Got me to the Moon!", 4)),
    ID("3") to listOf(Review(ID("1030"), 3)),
    ID("4") to listOf(Review(ID("1040"), 5), Review(ID("1041"), "Reusable!", 5), Review(ID("1042"), 5)),
    ID("5") to listOf(Review(ID("1050"), "Amazing! Would Fly Again!", 5), Review(ID("1051"), 5))
)

@KeyDirective(fields = FieldSet(value = "id"))
data class Product(val id: ID) {

    fun reviews(): List<Review> = reviews[id] ?: emptyList()
}

data class Review(val id: ID, val text: String? = null, val starRating: Int) {
    constructor(id: ID, starRating: Int) : this(id, null, starRating)
}
