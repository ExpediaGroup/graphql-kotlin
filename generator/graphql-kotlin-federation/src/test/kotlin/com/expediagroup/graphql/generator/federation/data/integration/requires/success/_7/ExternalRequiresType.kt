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

package com.expediagroup.graphql.generator.federation.data.integration.requires.success._7

import com.expediagroup.graphql.generator.federation.directives.ExternalDirective
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.federation.directives.RequiresDirective
import kotlin.properties.Delegates

/*
# @external information is applied to fields within type
type RecursiveExternalRequires @key(fields : "id") {
  externalType: ExternalType!
  description: String!
  id: String!
  shippingCost: String! @requires(fields : "complexType { weight }")
}

type ExternalType @external {
  allExternal: String
  weight: Float!
}
 */
@KeyDirective(fields = FieldSet("id"))
class ExternalRequiresType(val id: String, val description: String, val externalType: ExternalType) {

    @RequiresDirective(FieldSet("externalType { weight }"))
    fun shippingCost(): String = "$${externalType.weight * 9.99}"
}

@ExternalDirective
class ExternalType(val allExternal: String) {
    var weight: Double by Delegates.notNull()
}
