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

package com.expediagroup.graphql.generator.federation.data.integration.requires.success._3

import com.expediagroup.graphql.generator.federation.directives.ExternalDirective
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.federation.directives.RequiresDirective
import io.mockk.mockk
import kotlin.properties.Delegates

/*
# example of proper usage of @requires directive - @requires applied on a field returning interface
type RequiresSelectionOnInterface @key(fields : "id") {
  id: Int!
  review: Review! @requires(fields : "email")
  email: String! @external
}

interface Review {
  id: Int!
  text: String!
}

type ProductReview implements Review {
  id: Int!
  text: String!
}
 */
@KeyDirective(fields = FieldSet("id"))
data class RequiresSelectionOnInterface(val id: Int) {

    @ExternalDirective
    var email: String by Delegates.notNull()

    @RequiresDirective(FieldSet("email"))
    var review: Review = mockk()
}

interface Review {
    val id: Int
    val text: String
}

data class ProductReview(override val id: Int, override val text: String) : Review
