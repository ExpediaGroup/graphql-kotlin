/*
 * Copyright 2024 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.data.integration.requires.success._8

import com.expediagroup.graphql.generator.federation.directives.ExternalDirective
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.federation.directives.RequiresDirective
import kotlin.properties.Delegates

/*
# example of @requires directive with inline fragment on union type (issue #1939)
type RequiresInlineFragmentOnUnion @key(fields : "id") {
  id: Int!
  shippingLabel: String! @requires(fields : "animal { ... on Dog { breed } }")
  animal: Animal! @external
}

union Animal = Dog | Cat

type Dog {
  breed: String!
}

type Cat {
  color: String!
}
 */
@KeyDirective(fields = FieldSet("id"))
data class RequiresInlineFragmentOnUnion(val id: Int) {

    @ExternalDirective
    var animal: Animal by Delegates.notNull()

    @RequiresDirective(FieldSet("animal { ... on Dog { breed } }"))
    fun shippingLabel(): String = "label"
}

interface Animal

data class Dog(val breed: String) : Animal

data class Cat(val color: String) : Animal
