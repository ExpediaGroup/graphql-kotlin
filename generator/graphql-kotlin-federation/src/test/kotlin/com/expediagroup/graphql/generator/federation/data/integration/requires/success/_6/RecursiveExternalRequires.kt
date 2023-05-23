/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.data.integration.requires.success._6

import com.expediagroup.graphql.generator.federation.directives.ExternalDirective
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.federation.directives.RequiresDirective
import kotlin.properties.Delegates

/*
# @external information is applied recursively through parent fields
type RecursiveExternalRequires @key(fields : "id") {
  complexType: ComplexType! @external
  description: String!
  id: String!
  shippingCost: String! @requires(fields : "complexType { weight }")
}

type ComplexType {
  potentiallyExternal: String
  weight: Float!
}
 */
@KeyDirective(fields = FieldSet("id"))
class RecursiveExternalRequires(val id: String, val description: String, @ExternalDirective val complexType: ComplexType) {

    @RequiresDirective(FieldSet("complexType { weight }"))
    fun shippingCost(): String = "$${complexType.weight * 9.99}"
}

class ComplexType(val potentiallyExternal: String) {
    var weight: Double by Delegates.notNull()
}
