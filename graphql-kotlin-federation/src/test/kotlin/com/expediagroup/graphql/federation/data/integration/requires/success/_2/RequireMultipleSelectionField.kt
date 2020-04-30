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

package com.expediagroup.graphql.federation.data.integration.requires.success._2

import com.expediagroup.graphql.federation.directives.ExtendsDirective
import com.expediagroup.graphql.federation.directives.ExternalDirective
import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.directives.KeyDirective
import com.expediagroup.graphql.federation.directives.RequiresDirective
import com.expediagroup.graphql.federation.execution.FederatedTypeResolver
import graphql.schema.DataFetchingEnvironment
import kotlin.properties.Delegates

class RequireMultipleSelectionField {

    @KeyDirective(fields = FieldSet("id"))
    @ExtendsDirective
    data class Basket(@ExternalDirective val id: Int) {

        @ExternalDirective
        var items: List<BasketItem> by Delegates.notNull()

        @RequiresDirective(FieldSet("items { id timeStamp }"))
        fun image(): String {
            return items.joinToString("|")
        }
    }

    @KeyDirective(fields = FieldSet("id"))
    @ExtendsDirective
    data class BasketItem(@ExternalDirective val id: Int, @ExternalDirective val timeStamp: String)

    val basketResolver = object : FederatedTypeResolver<Basket> {
        override suspend fun resolve(environment: DataFetchingEnvironment, representations: List<Map<String, Any>>): List<Basket?> = representations.map {
            val id = it["id"]?.toString()?.toIntOrNull()
            val itemRepresnetations = it["items"] as? ArrayList<Map<String, Any>>
            val items = itemRepresnetations?.map { basketItemRepresentation ->
                BasketItem(basketItemRepresentation["id"] as Int, basketItemRepresentation["timeStamp"] as String)
            }

            val basket = Basket(id!!)
            basket.items = items!!
            basket
        }
    }
}
