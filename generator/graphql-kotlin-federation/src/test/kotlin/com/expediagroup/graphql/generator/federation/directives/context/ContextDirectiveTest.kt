/*
 * Copyright 2026 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.directives.context

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.directives.CONTEXT_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.ContextDirective
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC_URL_PREFIX
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ContextDirectiveTest {

    @Test
    fun `verify context directive definition for fed215`() {
        val expectedSchema =
            // language=GraphQL
            """
            schema @link(import : ["@key", "@context"], url : "https://specs.apollo.dev/federation/v2.15"){
              query: Query
            }

            "Defines a named context from which a field of the annotated type can be passed to a receiver of the context"
            directive @context(name: String!) repeatable on OBJECT | INTERFACE | UNION

            "Marks the field, argument, input field or enum value as deprecated"
            directive @deprecated(
                "The reason for the deprecation"
                reason: String! = "No longer supported"
              ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION

            "This directive disables error propagation when a non nullable field returns null for the given operation."
            directive @experimental_disableErrorPropagation on QUERY | MUTATION | SUBSCRIPTION

            "Directs the executor to include this field or fragment only when the `if` argument is true"
            directive @include(
                "Included when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Space separated list of primary keys needed to access federated object"
            directive @key(fields: federation__FieldSet!, resolvable: Boolean = true) repeatable on OBJECT | INTERFACE

            "Links definitions within the document to external schemas."
            directive @link(as: String, import: [link__Import], url: String!) repeatable on SCHEMA

            "Indicates an Input Object is a OneOf Input Object."
            directive @oneOf on INPUT_OBJECT

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

            union _Entity = Product

            type Product @context(name : "userContext") @key(fields : "id", resolvable : true) {
              id: String!
            }

            type Query {
              "Union of all types that use the @key directive, including both types native to the schema and extended types"
              _entities(representations: [_Any!]!): [_Entity]!
              _service: _Service!
              product: Product!
            }

            type _Service {
              sdl: String!
            }

            "Federation scalar type used to represent any external entities passed to _entities query."
            scalar _Any

            "Federation type representing set of fields"
            scalar federation__FieldSet

            scalar link__Import
            """.trimIndent()

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.context"),
            hooks = FederatedSchemaGeneratorHooks(emptyList()).apply {
                this.linkSpecs[FEDERATION_SPEC] = FederatedSchemaGeneratorHooks.LinkSpec(
                    namespace = FEDERATION_SPEC,
                    imports = mapOf("key" to "key", "context" to "context"),
                    url = "$FEDERATION_SPEC_URL_PREFIX/v2.15"
                )
            }
        )
        val schema = toFederatedSchema(config, listOf(TopLevelObject(Query())))
        Assertions.assertEquals(expectedSchema, schema.print().trim())

        val product = schema.getObjectType("Product")
        assertNotNull(product)
        assertTrue(product.hasAppliedDirective(CONTEXT_DIRECTIVE_NAME))
    }

    @Test
    fun `verify context directive is not created for federation v2_7`() {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.context"),
            hooks = FederatedSchemaGeneratorHooks(emptyList()).apply {
                this.linkSpecs[FEDERATION_SPEC] = FederatedSchemaGeneratorHooks.LinkSpec(
                    namespace = FEDERATION_SPEC,
                    imports = emptyMap(),
                    url = "$FEDERATION_SPEC_URL_PREFIX/v2.7"
                )
            }
        )
        val exception = Assertions.assertThrows(IllegalArgumentException::class.java) {
            toFederatedSchema(
                queries = listOf(TopLevelObject(Query())),
                config = config
            )
        }
        Assertions.assertEquals(
            "@context directive requires Federation 2.8 or later, but version https://specs.apollo.dev/federation/v2.7 was specified",
            exception.message
        )
    }

    class Query {
        fun product(): Product = Product("1")
    }

    @KeyDirective(FieldSet("id"))
    @ContextDirective(name = "userContext")
    class Product(val id: String)
}
