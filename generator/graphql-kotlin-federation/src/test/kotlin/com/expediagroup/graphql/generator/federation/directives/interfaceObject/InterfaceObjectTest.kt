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

package com.expediagroup.graphql.generator.federation.directives.interfaceObject

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC_URL_PREFIX
import com.expediagroup.graphql.generator.federation.directives.InterfaceObjectDirective
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import com.expediagroup.graphql.generator.scalars.ID
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class InterfaceObjectTest {

    @Test
    fun `verify interfaceObject directive is not created for federation v2_2`() {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.interfaceObject"),
            hooks = FederatedSchemaGeneratorHooks(emptyList()).apply {
                this.linkSpecs[FEDERATION_SPEC] = FederatedSchemaGeneratorHooks.LinkSpec(
                    namespace = FEDERATION_SPEC,
                    imports = emptyMap(),
                    url = "$FEDERATION_SPEC_URL_PREFIX/v2.2"
                )
            }
        )
        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            toFederatedSchema(
                queries = listOf(TopLevelObject(FooQuery())),
                config = config
            )
        }
        Assertions.assertEquals(
            "@interfaceObject directive requires Federation 2.3 or later, but version https://specs.apollo.dev/federation/v2.2 was specified",
            exception.message
        )
    }

    class FooQuery {
        fun foo(): MyInterface = Product(ID("123"), addedField = 42)
    }

    interface MyInterface {
        val id: ID
        val addedField: Int
    }

    @InterfaceObjectDirective
    data class Product(override val id: ID, override val addedField: Int) : MyInterface {
        fun reviews(): String = TODO()
    }
}
