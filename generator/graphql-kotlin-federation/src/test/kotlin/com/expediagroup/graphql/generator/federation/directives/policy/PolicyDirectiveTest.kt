/*
 * Copyright 2024 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.directives.policy

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.directives.POLICY_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.Policies
import com.expediagroup.graphql.generator.federation.directives.Policy
import com.expediagroup.graphql.generator.federation.directives.PolicyDirective
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class PolicyDirectiveTest {

    @Test
    fun `verify we can import federation spec using custom @link`() {
        val expectedSchema =
            """
            schema @link(url : "https://specs.apollo.dev/federation/v2.6"){
              query: Query
            }

            "Marks the field, argument, input field or enum value as deprecated"
            directive @deprecated(
                "The reason for the deprecation"
                reason: String = "No longer supported"
              ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION

            "Indicates to composition that the target element is restricted based on authorization policies that are evaluated in a Rhai script or coprocessor"
            directive @federation__policy(policies: [[federation__Policy]!]!) on SCALAR | OBJECT | FIELD_DEFINITION | INTERFACE | ENUM

            "Directs the executor to include this field or fragment only when the `if` argument is true"
            directive @include(
                "Included when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Links definitions within the document to external schemas."
            directive @link(as: String, import: [link__Import], url: String!) repeatable on SCHEMA

            "Directs the executor to skip this field or fragment when the `if` argument is true."
            directive @skip(
                "Skipped when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Exposes a URL that specifies the behaviour of this scalar."
            directive @specifiedBy(
                "The URL that specifies the behaviour of this scalar."
                url: String!
              ) on SCALAR

            type Query {
              _service: _Service!
              foo: String! @federation__policy(policies : [["policy1", "policy2"], ["policy3"]])
            }

            type _Service {
              sdl: String!
            }

            "Federation type representing authorization policy"
            scalar federation__Policy

            scalar link__Import
            """.trimIndent()

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.policy"),
            hooks = FederatedSchemaGeneratorHooks(emptyList())
        )

        val schema = toFederatedSchema(queries = listOf(TopLevelObject(FooQuery())), config = config)
        Assertions.assertEquals(expectedSchema, schema.print().trim())
        val query = schema.getObjectType("Query")
        assertNotNull(query)
        val fooQuery = query.getField("foo")
        assertNotNull(fooQuery)
        assertNotNull(fooQuery.hasAppliedDirective(POLICY_DIRECTIVE_NAME))
    }

    class FooQuery {
        @PolicyDirective(policies = [Policies([Policy("policy1"), Policy("policy2")]), Policies([Policy("policy3")])])
        fun foo(): String = TODO()
    }
}
