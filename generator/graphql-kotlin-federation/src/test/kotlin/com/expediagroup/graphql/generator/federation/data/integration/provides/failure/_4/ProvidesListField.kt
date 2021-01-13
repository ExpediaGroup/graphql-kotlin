/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.data.integration.provides.failure._4

import com.expediagroup.graphql.generator.federation.directives.ExtendsDirective
import com.expediagroup.graphql.generator.federation.directives.ExternalDirective
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.federation.directives.ProvidesDirective
import io.mockk.mockk

/*
# example invalid usage of @provides directive - provides references a list field
type ProvidesListField @key(fields : "id") {
  description: String!
  id: String!
  provided: ProvidedWithList! @provides(fields : "text")
}

type ProvidedWithList @extends @key(fields : "id") {
  id: String! @external
  text: [String!]! @external
}
 */
@KeyDirective(fields = FieldSet("id"))
class ProvidesListField(val id: String, val description: String) {

    @ProvidesDirective(fields = FieldSet("text"))
    fun provided() = ProvidedWithList(id, listOf("some text"))
}

@KeyDirective(fields = FieldSet("id"))
@ExtendsDirective
data class ProvidedWithList(
    @ExternalDirective val id: String,
    @ExternalDirective val text: List<String>
)

class ProvidesListFieldQuery {
    fun providesQuery(): ProvidesListField = mockk()
}
