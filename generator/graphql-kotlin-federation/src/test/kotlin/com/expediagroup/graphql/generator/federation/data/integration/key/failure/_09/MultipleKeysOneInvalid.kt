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

package com.expediagroup.graphql.generator.federation.data.integration.key.failure._09

import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import io.mockk.mockk

/*
# example invalid usage of @key directive - one field set references non-existent field
type MultipleKeysOneInvalid @key(fields : "id") @key(fields : "upc") {
  description: String!
  id: String!
}
 */
@KeyDirective(fields = FieldSet("id"))
@KeyDirective(fields = FieldSet("upc"))
data class MultipleKeysOneInvalid(val id: String, val description: String)

class MultipleKeysOneInvalidQuery {
    fun multipleKeysOneInvalid(): MultipleKeysOneInvalid = mockk()
}
