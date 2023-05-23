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

package com.expediagroup.graphql.generator.federation.data.integration.requires.success._5

import com.expediagroup.graphql.generator.federation.directives.ExternalDirective
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.federation.directives.RequiresDirective
import kotlin.properties.Delegates

/*
# only leaf fields have to be external when @requires references complex types
type LeafRequires @key(fields : "id") {
  complexType: ComplexType!
  description: String!
  id: String!
  shippingCost: String! @requires(fields : "complexType { weight }")
}

type ComplexType {
  localField: String
  weight: Float! @external
}
 */
@KeyDirective(fields = FieldSet("id"))
class LeafRequires(val id: String, val description: String, val complexType: ComplexType) {

    @RequiresDirective(FieldSet("complexType { weight }"))
    fun shippingCost(): String = "$${complexType.weight * 9.99}"
}

class ComplexType(val localField: String) {
    @ExternalDirective
    var weight: Double by Delegates.notNull()
}
