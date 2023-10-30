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
package com.expediagroup.graphql.generator.federation.data.integration.key.success._6

import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.scalars.ID
import io.mockk.mockk

/*
# example usage of a @key referencing parent
type Child @key(fields: "parent { id }") {
  parent: Parent
}

type Parent {
  id: ID!
  someChild: Child!
}

type Query {
  parent: Parent!
}
 */
data class Parent(
    val id: ID,
    val someChild: Child,
)

@KeyDirective(fields = FieldSet("parent { id }"))
data class Child(
    val parent: Parent,
)

class EntityReferencingParent {
    fun parent(): Parent = mockk()
}
