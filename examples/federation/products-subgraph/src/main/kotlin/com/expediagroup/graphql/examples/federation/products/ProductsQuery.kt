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

package com.expediagroup.graphql.examples.federation.products

import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class ProductsQuery : Query {

    private val products = mapOf(
        "1" to Product(ID("1"), "Saturn V", "The Original Super Heavy-Lift Rocket!"),
        "2" to Product(ID("2"), "Lunar Module"),
        "3" to Product(ID("3"), "Space Shuttle"),
        "4" to Product(ID("4"), "Falcon 9", "Reusable Medium-Lift Rocket"),
        "5" to Product(ID("5"), "Dragon", "Reusable Medium-Lift Rocket"),
        "6" to Product(ID("6"), "Starship", "Super Heavy-Lift Reusable Launch Vehicle")
    )

    fun product(id: ID): Product? = products[id.value]

    fun products(): List<Product> = products.values.toList()
}

@KeyDirective(fields = FieldSet(value = "id"))
data class Product(val id: ID, val name: String, val description: String? = null)
