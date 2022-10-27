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

package com.expediagroup.graphql.generator.federation.data.integration.provides.success._4

import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.federation.directives.ProvidesDirective
import io.mockk.mockk

/*
# example use case of @provides directive - type exposes an object with a list field
#   from other federated type that can be resolved locally
type resolves list field locally
type ProvidesList @key(fields : "id") {
  description: String!
  id: String!
  provided: ProvidedType! @provides(fields : "text")
}

type ProvidedType @key(fields : "id") {
  id: String!
  text: [String!]!
}
 */
@KeyDirective(fields = FieldSet("id"))
class ProvidesList(val id: String, val description: String) {

    @ProvidesDirective(fields = FieldSet("text"))
    fun provided(): ProvidedType = mockk()
}

@KeyDirective(fields = FieldSet("id"))
data class ProvidedType(
    val id: String,
    val text: List<String>
)

class ProvidesListQuery {
    fun providesQuery(): ProvidesList = mockk()
}
