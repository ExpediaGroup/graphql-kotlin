package com.expedia.graphql.federation.validation

import com.expedia.graphql.federation.FederatedSchemaGenerator
import com.expedia.graphql.federation.FederatedSchemaGeneratorConfig
import com.expedia.graphql.federation.FederatedSchemaGeneratorHooks
import com.expedia.graphql.federation.FederatedSchemaValidator
import com.expedia.graphql.federation.exception.InvalidFederatedSchema
import com.expedia.graphql.federation.directives.ExtendsDirective
import com.expedia.graphql.federation.directives.ExternalDirective
import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import graphql.schema.GraphQLObjectType
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FederatedSchemaValidatorKeyDirectiveTest {
    private val validator = FederatedSchemaValidator()
    private lateinit var schemaGenerator: FederatedSchemaGenerator

    /**
     * Parameterized JUnit5 test source that provides arguments for validating @key directive.
     *
     * @sample <testName>, <testClass>, (Optional)<expectedValidationError>
     */
    fun keyDirectiveValidations() = Stream.of(
        Arguments.of("[OK] base type with simple @key", SimpleKey::class, null),
        Arguments.of("[OK] federated @extend type with simple @key", FederatedSimpleKey::class, null),
        Arguments.of("[OK] base type with @key referencing multiple fields", KeyWithMultipleFields::class, null),
        Arguments.of("[OK] federated @extend type with @key referencing multiple fields", FederatedKeyWithMultipleFields::class, null),
        Arguments.of("[OK] base type with @key referencing nested fields", KeyWithNestedFields::class, null),
        Arguments.of("[OK] federated @extend type with @key referencing nested fields", FederatedKeyWithNestedFields::class, null),
        Arguments.of("[ERROR] @external federated type without @key directive", FederatedMissingKey::class, "Invalid federated schema:\n" +
            " - @key directive is missing on federated FederatedMissingKey type"),
        Arguments.of("[ERROR] base type with empty @key field set", KeyMissingFieldSelection::class, "Invalid federated schema:\n" +
            " - @key directive on KeyMissingFieldSelection is missing field information"),
        Arguments.of("[ERROR] @key references non-existent fields", InvalidKey::class, "Invalid federated schema:\n" +
            " - @key(fields = id) directive on InvalidKey specifies invalid field set - field set specifies fields that do not exist"),
        Arguments.of("[ERROR] base type references @external fields in @key", BaseKeyReferencingExternalFields::class, "Invalid federated schema:\n" +
            " - @key(fields = id) directive on BaseKeyReferencingExternalFields specifies invalid field set - type incorrectly references external field=id\n" +
            " - base BaseKeyReferencingExternalFields type has fields marked with @external directive, fields=[id]"),
        Arguments.of("[ERROR] federated @extend type references local fields in @key", ExternalKeyReferencingLocalField::class, "Invalid federated schema:\n" +
            " - @key(fields = id) directive on ExternalKeyReferencingLocalField specifies invalid field set - extended type incorrectly references local field=id"),
        Arguments.of("[ERROR] @key references list", KeyReferencingList::class, "Invalid federated schema:\n" +
            " - @key(fields = id) directive on KeyReferencingList specifies invalid field set - field set references GraphQLList, field=id"),
        Arguments.of("[ERROR] @key references interface", KeyReferencingInterface::class, "Invalid federated schema:\n" +
            " - @key(fields = id) directive on KeyReferencingInterface specifies invalid field set - field set references GraphQLInterfaceType, field=id"),
        Arguments.of("[ERROR] @key references union", KeyReferencingUnion::class, "Invalid federated schema:\n" +
            " - @key(fields = id) directive on KeyReferencingUnion specifies invalid field set - field set references GraphQLUnionType, field=id"),
        Arguments.of("[ERROR] @key references nested value from scalar", NestedKeyReferencingScalar::class, "Invalid federated schema:\n" +
            " - @key(fields = id { uuid }) directive on NestedKeyReferencingScalar specifies invalid field set - field set defines nested selection set on unsupported type\n" +
            " - @key(fields = id { uuid }) directive on NestedKeyReferencingScalar specifies invalid field set - field set specifies fields that do not exist")
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
    @MethodSource("keyDirectiveValidations")
    @Suppress("UnusedPrivateMember")
    fun `validate @key directive`(testCase: String, targetClass: KClass<*>, expectedError: String?) {
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
    private data class SimpleKey(val id: String, val description: String)

    /*
    type FederatedSimpleKey @extends @key(fields : "id") {
      description: String!
      id: String! @external
    }
     */
    @KeyDirective(fields = FieldSet("id"))
    @ExtendsDirective
    private data class FederatedSimpleKey(@property:ExternalDirective val id: String, val description: String)

    /*
    type KeyWithMultipleFields @key(fields : "id type") {
      description: String!
      id: String!
      type: String!
    }
     */
    @KeyDirective(fields = FieldSet("id type"))
    private data class KeyWithMultipleFields(val id: String, val type: String, val description: String)

    /*
    type FederatedKeyWithMultipleFields @extends @key(fields : "id type") {
      description: String!
      id: String! @external
      type: String! @external
    }
     */
    @KeyDirective(fields = FieldSet("id type"))
    @ExtendsDirective
    private data class FederatedKeyWithMultipleFields(@property:ExternalDirective val id: String, @property:ExternalDirective val type: String, val description: String)

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
    private data class KeyWithNestedFields(val id: NestedId, val description: String)

    private data class NestedId(val uuid: String)

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
    private data class FederatedKeyWithNestedFields(@property:ExternalDirective val id: FederatedNestedId, val description: String)

    @KeyDirective(fields = FieldSet("uuid"))
    @ExtendsDirective
    private data class FederatedNestedId(@property:ExternalDirective val uuid: String)

    /*
    type FederatedMissingKey @extends {
      description: String!
      id: String! @external
    }
     */
    @ExtendsDirective
    private data class FederatedMissingKey(@property:ExternalDirective val id: String, val description: String)

    /*
    type KeyMissingFieldSelection @key(fields : "") {
      description: String!
      id: String!
    }
     */
    @KeyDirective(FieldSet(""))
    private data class KeyMissingFieldSelection(val id: String, val description: String)

    /*
    type InvalidKey @key(fields : "id") {
      description: String!
      productId: String!
    }
     */
    @KeyDirective(fields = FieldSet("id"))
    private data class InvalidKey(val productId: String, val description: String)

    /*
    type BaseKeyReferencingExternalFields @key(fields : "id") {
      description: String!
      id: String! @external
    }
     */
    @KeyDirective(fields = FieldSet("id"))
    data class BaseKeyReferencingExternalFields(@property:ExternalDirective val id: String, val description: String)

    /*
    type ExternalKeyReferencingLocalField @extends @key(fields : "id") {
      description: String!
      id: String!
    }
     */
    @KeyDirective(fields = FieldSet("id"))
    @ExtendsDirective
    private data class ExternalKeyReferencingLocalField(val id: String, val description: String)

    /*
    type KeyReferencingList @key(fields : "id") {
      description: String!
      id: [String!]!
    }
     */
    @KeyDirective(fields = FieldSet("id"))
    private data class KeyReferencingList(val id: List<String>, val description: String)

    /*
    type KeyReferencingInterface @key(fields : "id") {
      description: String!
      id: KeyInterface!
    }

    interface KeyInterface {
      id: String!
    }
     */
    @KeyDirective(fields = FieldSet("id"))
    private data class KeyReferencingInterface(val id: KeyInterface, val description: String)

    private interface KeyInterface {
        val id: String
    }

    /*
    type KeyReferencingUnion @key(fields : "id") {
      description: String!
      id: KeyUnion!
    }

    type Key {
      id: String!
    }

    union KeyUnion = Key
     */
    @KeyDirective(fields = FieldSet("id"))
    private data class KeyReferencingUnion(val id: KeyUnion, val description: String)

    private interface KeyUnion

    @Suppress("UnusedPrivateClass")
    private data class Key(val id: String) : KeyUnion

    /*
    type NestedKeyReferencingScalar @key(fields : "id { uuid }") {
      description: String!
      id: String!
    }
     */
    @KeyDirective(fields = FieldSet("id { uuid }"))
    private data class NestedKeyReferencingScalar(val id: String, val description: String)
}
