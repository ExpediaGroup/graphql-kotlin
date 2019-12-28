/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.federation.validation.requires

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.federation.FederatedSchemaGenerator
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.federation.FederatedSchemaValidator
import com.expediagroup.graphql.federation.directives.ExtendsDirective
import com.expediagroup.graphql.federation.directives.ExternalDirective
import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.directives.KeyDirective
import com.expediagroup.graphql.federation.directives.RequiresDirective
import com.expediagroup.graphql.federation.exception.InvalidFederatedSchema
import graphql.schema.GraphQLObjectType
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.properties.Delegates
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FederatedSchemaValidatorRequiresDirectiveTest {
    private val validator = FederatedSchemaValidator()
    private lateinit var schemaGenerator: FederatedSchemaGenerator

    /**
     * Parameterized JUnit5 test source that provides arguments for validating @requires directive.
     *
     * @sample <testName>, <testClass>, (Optional)<expectedValidationError>
     */
    fun requiresDirectiveValidations() = Stream.of(
        Arguments.of("[OK] @requires references valid fields", SimpleRequiresQuery::class, SimpleRequires::class, null),
        Arguments.of("[ERROR] @requires references local fields", RequiresLocalFieldQuery::class, RequiresLocalField::class, "Invalid federated schema:\n" +
            " - @requires(fields = weight) directive on RequiresLocalField.shippingCost specifies invalid field set - extended type incorrectly references local field=weight"),
        Arguments.of("[ERROR] @requires references non-existent fields", RequiresNonExistentFieldQuery::class, RequiresNonExistentField::class, "Invalid federated schema:\n" +
            " - @requires(fields = zipCode) directive on RequiresNonExistentField.shippingCost specifies invalid field set - field set specifies fields that do not exist"),
        Arguments.of("[ERROR] @requires declared on local type", RequiresOnLocalTypeQuery::class, RequiresOnLocalType::class, "Invalid federated schema:\n" +
            " - base RequiresOnLocalType type has fields marked with @requires directive, validatedField=shippingCost")
    )

    @BeforeEach
    fun beforeTest() {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.federation.validation.requires"),
            hooks = FederatedSchemaGeneratorHooks(mockk())
        )
        schemaGenerator = FederatedSchemaGenerator(config)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("requiresDirectiveValidations")
    @Suppress("UnusedPrivateMember")
    fun `validate @requires directive`(testCase: String, targetQueryClass: KClass<*>, federatedClass: KClass<*>, expectedError: String?) {
        // TODO: This is failing because createType drops annonation information
        val queries = listOf(TopLevelObject(targetQueryClass))
        val schema = schemaGenerator.generate(queries)
        val validatedType = schema.getType(federatedClass.simpleName) as? GraphQLObjectType
        assertNotNull(validatedType)
        assertEquals(targetQueryClass.simpleName, validatedType.name)

        if (expectedError != null) {
            val exception = assertFailsWith(InvalidFederatedSchema::class) {
                validator.validateGraphQLType(validatedType)
            }
            assertEquals(expectedError, exception.message)
        } else {
            validator.validateGraphQLType(validatedType)
            assertNotNull(validatedType.getDirective("key"))
            val weightField = validatedType.getFieldDefinition("weight")
            assertNotNull(weightField)
            assertNotNull(weightField.getDirective("external"))
            val requiresField = validatedType.getFieldDefinition("shippingCost")
            assertNotNull(requiresField)
            assertNotNull(requiresField.getDirective("requires"))
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

    /*
    type RequiresLocalField @extends @key(fields : "id") {
      description: String!
      id: String! @external
      shippingCost: String! @requires(fields : "weight")
      weight: Float!
    }
     */
    @KeyDirective(fields = FieldSet("id"))
    @ExtendsDirective
    class RequiresLocalField(@ExternalDirective val id: String, val description: String) {

        var weight: Double = 0.0

        @RequiresDirective(FieldSet("weight"))
        fun shippingCost(): String = "$${weight * 9.99}"
    }

    class RequiresLocalFieldQuery {
        fun requiresLocalField() = RequiresLocalField("1", "foo")
    }

    /*
    type RequiresNonExistentField @extends @key(fields : "id") {
      description: String!
      id: String! @external
      shippingCost: String! @requires(fields : "weight")
      weight: Float! @external
    }
     */
    @KeyDirective(fields = FieldSet("id"))
    @ExtendsDirective
    class RequiresNonExistentField(@ExternalDirective val id: String, val description: String) {

        @ExternalDirective
        var weight: Double by Delegates.notNull()

        @RequiresDirective(FieldSet("zipCode"))
        fun shippingCost(): String = "$${weight * 9.99}"
    }

    class RequiresNonExistentFieldQuery {
        fun requiresNonExistentField() = RequiresNonExistentField("1", "foo")
    }

    /*
    type RequiresOnLocalType @key(fields : "id") {
      description: String!
      id: String!
      shippingCost: String! @requires(fields : "weight")
      weight: Float!
    }
     */
    @KeyDirective(fields = FieldSet("id"))
    class RequiresOnLocalType(val id: String, val description: String) {
        var weight: Double = 0.0

        @RequiresDirective(FieldSet("weight"))
        fun shippingCost(): String = "$${weight * 9.99}"
    }

    class RequiresOnLocalTypeQuery {
        fun requiresOnLocalType() = RequiresOnLocalType("1", "foo")
    }
}
