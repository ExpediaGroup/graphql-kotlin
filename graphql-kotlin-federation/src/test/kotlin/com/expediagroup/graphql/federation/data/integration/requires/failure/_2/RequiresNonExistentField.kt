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

package com.expediagroup.graphql.federation.data.integration.requires.failure._2

import com.expediagroup.graphql.federation.directives.ExtendsDirective
import com.expediagroup.graphql.federation.directives.ExternalDirective
import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.directives.KeyDirective
import com.expediagroup.graphql.federation.directives.RequiresDirective
import kotlin.properties.Delegates

/*
# example of invalid usage of @requires directive when it references non existent field
type RequiresNonExistentField @extends @key(fields : "id") {
  description: String!
  id: String! @external
  shippingCost: String! @requires(fields : "zipCode")
  weight: Float! @external
}
 */
@KeyDirective(fields = FieldSet("id"))
@ExtendsDirective
class RequiresNonExistentField(@ExternalDirective val id: String, val description: String) {

    @ExternalDirective
    var weight: Double by Delegates.notNull()

    @RequiresDirective(FieldSet("zipCode"))
    fun shippingCost(): String = "$${weight * 9.99}"
}
