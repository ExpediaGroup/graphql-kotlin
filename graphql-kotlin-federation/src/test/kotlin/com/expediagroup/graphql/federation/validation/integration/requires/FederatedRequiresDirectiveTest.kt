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

package com.expediagroup.graphql.federation.validation.integration.requires

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.federation.directives.ExtendsDirective
import com.expediagroup.graphql.federation.directives.ExternalDirective
import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.directives.KeyDirective
import com.expediagroup.graphql.federation.directives.RequiresDirective
import com.expediagroup.graphql.federation.toFederatedSchema
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.properties.Delegates

class FederatedRequiresDirectiveTest {

    private val config = FederatedSchemaGeneratorConfig(
        supportedPackages = listOf("com.expediagroup.graphql.federation.validation.integration.requires"),
        hooks = FederatedSchemaGeneratorHooks(mockk())
    )

    @Test
    fun simpleRequires() {
        assertDoesNotThrow {
            val queries = listOf(TopLevelObject(SimpleRequiresQuery()))
            toFederatedSchema(config, queries)
        }
    }

    // ======================= TEST DATA ===========
    /*
    type SimpleRequires @extends @key(fields : "id") {
      description: String!
      id: String! @external
      shippingCost: String! @requires(fields : "weight")
      weight: Float! @external
    }
     */
    @KeyDirective(fields = FieldSet("id"))
    @ExtendsDirective
    class SimpleRequires(@ExternalDirective val id: String, val description: String) {
        @ExternalDirective
        var weight: Double by Delegates.notNull()

        @RequiresDirective(FieldSet("weight"))
        fun shippingCost(): String = "$${weight * 9.99}"
    }

    class SimpleRequiresQuery {
        fun simpleRequires() = SimpleRequires("1", "foo")
    }
}
