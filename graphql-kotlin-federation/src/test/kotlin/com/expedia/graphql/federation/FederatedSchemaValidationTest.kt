package com.expedia.graphql.federation

import com.expedia.graphql.TopLevelObject
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import test.data.queries.Query
import test.data.queries.base.emptykey.EmptyKeyQuery
import test.data.queries.base.external.BaseReferencingExternalKeyQuery
import test.data.queries.base.invalidkey.InvalidKeyQuery
import test.data.queries.base.multikey.MultiKeyQuery
import test.data.queries.base.nestedkey.NestedKeyQuery
import test.data.queries.base.simplekey.SimpleKeyQuery
import test.data.queries.external.basetype.ExtensionReferencingLocalKeyQuery
import test.data.queries.external.emptykey.FederatedEmptyKeyQuery
import test.data.queries.external.invalidkey.FederatedInvalidKeyQuery
import test.data.queries.external.multikey.FederatedMultiKeyQuery
import test.data.queries.external.nestedkey.FederatedNestedKeyQuery
import test.data.queries.external.nokey.FederatedMissingKeyQuery
import test.data.queries.external.simplekey.FederatedSimpleKeyQuery
import test.data.queries.provides.localtype.ProvidesOnBaseTypeQuery
import test.data.queries.provides.valid.SimpleProvidesQuery
import test.data.queries.requires.basetype.RequiresOnBaseTypeQuery
import test.data.queries.requires.invalidkey.RequiresInvalidFieldQuery
import test.data.queries.requires.localkey.RequiresLocalFieldQuery
import test.data.queries.requires.valid.SimpleRequiresQuery
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FederatedSchemaValidationTest {

    // JUnit5 parameterized test requires static methods for sources
    companion object {
        @JvmStatic
        fun validQueries() = listOf(
            SimpleKeyQuery(),
            FederatedSimpleKeyQuery(),
            MultiKeyQuery(),
            FederatedMultiKeyQuery(),
            NestedKeyQuery(),
            FederatedNestedKeyQuery(),
            SimpleRequiresQuery(),
            SimpleProvidesQuery()
        )

        // pairs of target query to load + expected error message
        @JvmStatic
        fun invalidQueries() = Stream.of(
            Arguments.of(FederatedMissingKeyQuery(), "Invalid federated schema:\n" +
                " - @key directive is missing on federated Product type"),
            Arguments.of(EmptyKeyQuery(), "Invalid federated schema:\n" +
                " - @key directive on Product is missing field information"),
            Arguments.of(FederatedEmptyKeyQuery(), "Invalid federated schema:\n" +
                " - @key directive on Product is missing field information"),
            Arguments.of(InvalidKeyQuery(), "Invalid federated schema:\n" +
                " - @key(fields = id) directive on Product specifies invalid field set - field set specifies fields that do not exist"),
            Arguments.of(BaseReferencingExternalKeyQuery(), "Invalid federated schema:\n" +
                " - @key(fields = id) directive on Product specifies invalid field set - type incorrectly references external/base fields\n" +
                " - base Product type has fields marked with @external directive, fields=[id]"),
            Arguments.of(FederatedInvalidKeyQuery(), "Invalid federated schema:\n" +
                " - @key(fields = id) directive on Product specifies invalid field set - field set specifies fields that do not exist"),
            Arguments.of(ExtensionReferencingLocalKeyQuery(), "Invalid federated schema:\n" +
                " - @key(fields = id) directive on Product specifies invalid field set - type incorrectly references external/base fields"),
            Arguments.of(RequiresLocalFieldQuery(), "Invalid federated schema:\n" +
                " - @requires(fields = weight) directive on Product.shippingCost specifies invalid field set - type incorrectly references external/base fields"),
            Arguments.of(RequiresInvalidFieldQuery(), "Invalid federated schema:\n" +
                " - @requires(fields = zipCode) directive on Product.shippingCost specifies invalid field set - field set specifies fields that do not exist"),
            Arguments.of(RequiresOnBaseTypeQuery(), "Invalid federated schema:\n" +
                " - base Product type has fields marked with @requires directive, fields=[shippingCost]"),
            Arguments.of(ProvidesOnBaseTypeQuery(), "Invalid federated schema:\n" +
                " - @provides directive is specified on a Product topReview field references local object")
        )
    }

    @ParameterizedTest
    @MethodSource("validQueries")
    fun `generate valid federated schema`(query: Query) {
        val targetPackage = query.javaClass.`package`.name
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf(targetPackage),
            hooks = FederatedSchemaGeneratorHooks(FederatedTypeRegistry(emptyMap()))
        )

        val schema = toFederatedSchema(config, listOf(TopLevelObject(query)))
        val productType = schema.getObjectType("Product")
        assertNotNull(productType)
        assertNotNull(productType.getDirective("key"))

        val entityUnion = schema.getType("_Entity") as? GraphQLUnionType
        assertNotNull(entityUnion)
        assertTrue(entityUnion.types.contains(productType))
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidQueries")
    fun `fail to generate invalid federated schema`(query: Query, expectedError: String) {
        val targetPackage = query.javaClass.`package`.name
        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf(targetPackage),
            hooks = FederatedSchemaGeneratorHooks(FederatedTypeRegistry(emptyMap()))
        )

        val exception = assertThrows<InvalidFederatedSchema> {
            toFederatedSchema(config, listOf(TopLevelObject(query)))
        }
        assertEquals(expectedError, exception.message)
    }
}
