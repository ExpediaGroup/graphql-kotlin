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

package com.expediagroup.graphql.federation.validation.integration.provides

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.federation.directives.ExtendsDirective
import com.expediagroup.graphql.federation.directives.ExternalDirective
import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.directives.KeyDirective
import com.expediagroup.graphql.federation.directives.ProvidesDirective
import com.expediagroup.graphql.federation.toFederatedSchema
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class FederatedProvidesDirectiveTest {

    val config = FederatedSchemaGeneratorConfig(
        supportedPackages = listOf("com.expediagroup.graphql.federation.validation.integration.provides"),
        hooks = FederatedSchemaGeneratorHooks(mockk())
    )

    @Test
    fun simpleProvidesQuery() {
        assertDoesNotThrow {
            val queries = listOf(TopLevelObject(SimpleProvidesQuery()))
            toFederatedSchema(config, queries)
        }
    }

    @Test
    fun providesListQuery() {
        assertDoesNotThrow {
            val queries = listOf(TopLevelObject(ProvidesListQuery()))
            toFederatedSchema(config, queries)
        }
    }

    // =============== TEST DATA ===============
    /*
        type SimpleProvides @key(fields : "id") {
          description: String!
          id: String!
          provided: ProvidedType! @provides(fields : "text")
        }
        type ProvidedType @extends @key(fields : "id") {
          id: String! @external
          text: String! @external
        }
     */
    @KeyDirective(fields = FieldSet("id"))
    class SimpleProvides(val id: String, val description: String) {

        @ProvidesDirective(fields = FieldSet("text"))
        fun provided() = ProvidedType(id, "some text")
    }

    @KeyDirective(fields = FieldSet("id"))
    @ExtendsDirective
    data class ProvidedType(
        @ExternalDirective val id: String,
        @ExternalDirective val text: String
    )

    class SimpleProvidesQuery {
        fun simpleProvides() = SimpleProvides("1", "foo")
    }

    /*
        type SimpleProvides @key(fields : "id") {
          description: String!
          id: String!
          provided: [ProvidedType!]! @provides(fields : "text")
        }
        type ProvidedType @extends @key(fields : "id") {
          id: String! @external
          text: String! @external
        }
    */
    @KeyDirective(fields = FieldSet("id"))
    class ProvidesList(val id: String, val description: String) {

        @ProvidesDirective(fields = FieldSet("text"))
        fun provided(): List<ProvidedType> = listOf(ProvidedType(id, "some text"))
    }

    class ProvidesListQuery {
        fun providesList() = ProvidesList("1", "foo")
    }
}
