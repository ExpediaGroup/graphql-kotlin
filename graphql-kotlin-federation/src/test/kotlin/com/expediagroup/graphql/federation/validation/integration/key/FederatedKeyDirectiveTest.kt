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

package com.expediagroup.graphql.federation.validation.integration.key

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.federation.directives.ExtendsDirective
import com.expediagroup.graphql.federation.directives.ExternalDirective
import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.directives.KeyDirective
import com.expediagroup.graphql.federation.execution.FederatedTypeRegistry
import com.expediagroup.graphql.federation.toFederatedSchema
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class FederatedKeyDirectiveTest {

    private val config = FederatedSchemaGeneratorConfig(
        supportedPackages = listOf("com.expediagroup.graphql.federation.validation.integration.key"),
        hooks = FederatedSchemaGeneratorHooks(FederatedTypeRegistry())
    )

    @Test
    fun simpleKeyQuery() {
        assertDoesNotThrow {
            val queries = listOf(TopLevelObject(SimpleKeyQuery()))
            toFederatedSchema(config, queries)
        }
    }

    @Test
    fun federatedSimpleKeyQuery() {
        assertDoesNotThrow {
            val queries = listOf(TopLevelObject(FederatedSimpleKeyQuery()))
            toFederatedSchema(config, queries)
        }
    }

    @Test
    fun keyWithMultipleFieldsQuery() {
        assertDoesNotThrow {
            val queries = listOf(TopLevelObject(KeyWithMultipleFieldsQuery()))
            toFederatedSchema(config, queries)
        }
    }

    @Test
    fun federatedKeyWithMultipleFieldsQuery() {
        assertDoesNotThrow {
            val queries = listOf(TopLevelObject(FederatedKeyWithMultipleFieldsQuery()))
            toFederatedSchema(config, queries)
        }
    }

    @Test
    fun keyWithNestedFieldsQuey() {
        assertDoesNotThrow {
            val queries = listOf(TopLevelObject(KeyWithNestedFieldsQuey()))
            toFederatedSchema(config, queries)
        }
    }

    @Test
    fun federatedKeyWithNestedFieldsQuery() {
        assertDoesNotThrow {
            val queries = listOf(TopLevelObject(FederatedKeyWithNestedFieldsQuery()))
            toFederatedSchema(config, queries)
        }
    }

    // ======================= TEST DATA ===========
    /*
    type SimpleKey @key(fields : "id") {
      description: String!
      id: String!
    }
     */
    @KeyDirective(fields = FieldSet("id"))
    data class SimpleKey(val id: String, val description: String)

    class SimpleKeyQuery {
        fun simpleKey() = SimpleKey("1", "foo")
    }

    /*
    type FederatedSimpleKey @extends @key(fields : "id") {
      description: String!
      id: String! @external
    }
     */
    @KeyDirective(fields = FieldSet("id"))
    @ExtendsDirective
    data class FederatedSimpleKey(@ExternalDirective val id: String, val description: String)

    class FederatedSimpleKeyQuery {
        fun federatedSimpleKey() = FederatedSimpleKey("1", "foo")
    }

    /*
    type KeyWithMultipleFields @key(fields : "id type") {
      description: String!
      id: String!
      type: String!
    }
     */
    @KeyDirective(fields = FieldSet("id type"))
    data class KeyWithMultipleFields(val id: String, val type: String, val description: String)

    class KeyWithMultipleFieldsQuery {
        fun keyWithMultipleFields() = KeyWithMultipleFields("1", "foo", "bar")
    }

    /*
    type FederatedKeyWithMultipleFields @extends @key(fields : "id type") {
      description: String!
      id: String! @external
      type: String! @external
    }
     */
    @KeyDirective(fields = FieldSet("id type"))
    @ExtendsDirective
    data class FederatedKeyWithMultipleFields(@ExternalDirective val id: String, @ExternalDirective val type: String, val description: String)

    class FederatedKeyWithMultipleFieldsQuery {
        fun federatedKeyWithMultipleFields() = FederatedKeyWithMultipleFields("1", "foo", "bar")
    }

    /*
    type KeyWithNestedFields @key(fields : "id { uuid }") {
      description: String!
      id: BaseNestedId!
    }
    type NestedId {
      uuid: String!
    }
     */
    @KeyDirective(fields = FieldSet("id { uuid }"))
    data class KeyWithNestedFields(val id: NestedId, val description: String)

    data class NestedId(val uuid: String)

    class KeyWithNestedFieldsQuey {
        fun keyWithNestedFields() = KeyWithNestedFields(NestedId("1"), "foo")
    }

    /*
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

    class FederatedKeyWithNestedFieldsQuery {
        fun federatedKeyWithNestedFields() = FederatedKeyWithNestedFields(FederatedNestedId("1"), "foo")
    }

}
