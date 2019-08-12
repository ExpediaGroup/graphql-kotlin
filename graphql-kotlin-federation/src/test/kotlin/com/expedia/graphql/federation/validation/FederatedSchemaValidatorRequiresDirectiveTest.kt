package com.expedia.graphql.federation.validation

import com.expedia.graphql.federation.FederatedSchemaGenerator
import com.expedia.graphql.federation.FederatedSchemaGeneratorConfig
import com.expedia.graphql.federation.FederatedSchemaGeneratorHooks
import com.expedia.graphql.federation.FederatedSchemaValidator
import com.expedia.graphql.federation.InvalidFederatedSchema
import com.expedia.graphql.federation.directives.ExtendsDirective
import com.expedia.graphql.federation.directives.ExternalDirective
import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import com.expedia.graphql.federation.directives.RequiresDirective
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
        Arguments.of("[OK] @requires references valid fields", SimpleRequires::class, null),
        Arguments.of("[ERROR] @requires references local fields", RequiresLocalField::class, "Invalid federated schema:\n" +
            " - @requires(fields = weight) directive on RequiresLocalField.shippingCost specifies invalid field set - extended type incorrectly references local field=weight"),
        Arguments.of("[ERROR] @requires references non-existent fields", RequiresNonExistentField::class, "Invalid federated schema:\n" +
            " - @requires(fields = zipCode) directive on RequiresNonExistentField.shippingCost specifies invalid field set - field set specifies fields that do not exist"),
        Arguments.of("[ERROR] @requires declared on local type", RequiresOnLocalType::class, "Invalid federated schema:\n" +
            " - base RequiresOnLocalType type has fields marked with @requires directive, validatedField=shippingCost")
    )

    @BeforeEach
    fun beforeTest() {
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expedia"),
            hooks = FederatedSchemaGeneratorHooks(mockk())
        )
        schemaGenerator = FederatedSchemaGenerator(config)
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("requiresDirectiveValidations")
    @Suppress("UnusedPrivateMember")
    fun `validate @requires directive`(testCase: String, targetClass: KClass<*>, expectedError: String?) {
        val validatedType = schemaGenerator.objectType(targetClass) as? GraphQLObjectType
        assertNotNull(validatedType)
        assertEquals(targetClass.simpleName, validatedType.name)

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
    private class SimpleRequires(@property:ExternalDirective val id: String, val description: String) {
        @ExternalDirective
        var weight: Double by Delegates.notNull()

        @RequiresDirective(FieldSet("weight"))
        fun shippingCost(): String = "$${weight * 9.99}"
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
    private class RequiresLocalField(@property:ExternalDirective val id: String, val description: String) {

        var weight: Double = 0.0

        @RequiresDirective(FieldSet("weight"))
        fun shippingCost(): String = "$${weight * 9.99}"
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
    class RequiresNonExistentField(@property:ExternalDirective val id: String, val description: String) {

        @ExternalDirective
        var weight: Double by Delegates.notNull()

        @RequiresDirective(FieldSet("zipCode"))
        fun shippingCost(): String = "$${weight * 9.99}"
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
}
