/*
 * Copyright 2025 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.directives.authenticated

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.directives.AuthenticatedDirective
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC_URL_PREFIX
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class AuthenticatedTest {

    @Test
    fun `verify authenticated directive is not created for federation v2_4`() {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.authenticated"),
            hooks = FederatedSchemaGeneratorHooks(emptyList()).apply {
                this.linkSpecs[FEDERATION_SPEC] = FederatedSchemaGeneratorHooks.LinkSpec(
                    namespace = FEDERATION_SPEC,
                    imports = emptyMap(),
                    url = "$FEDERATION_SPEC_URL_PREFIX/v2.4"
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
            "@authenticated directive requires Federation 2.5 or later, but version https://specs.apollo.dev/federation/v2.4 was specified",
            exception.message
        )
    }

    class FooQuery {
        @AuthenticatedDirective
        fun foo(): String = TODO()
    }
}
