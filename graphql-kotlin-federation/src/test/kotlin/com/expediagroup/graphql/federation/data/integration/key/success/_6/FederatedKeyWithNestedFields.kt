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

package com.expediagroup.graphql.federation.data.integration.key.success._6

import com.expediagroup.graphql.federation.directives.ExtendsDirective
import com.expediagroup.graphql.federation.directives.ExternalDirective
import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.directives.KeyDirective

/*
# example usage of a valid @key directive referencing nested/complex object key on a federated type
type FederatedKeyWithNestedFields @extends @key(fields : "id { uuid }") {
  description: String!
  id: FederatedNestedId! @external
}

type FederatedNestedId @extends @key(fields : "uuid") {
  uuid: String! @external
}
 */
@KeyDirective(fields = FieldSet("id { uuid }"))
@ExtendsDirective
data class FederatedKeyWithNestedFields(@ExternalDirective val id: FederatedNestedId, val description: String)

@KeyDirective(fields = FieldSet("uuid"))
@ExtendsDirective
data class FederatedNestedId(@ExternalDirective val uuid: String)
