package com.expediagroup.graphql.generator.federation.directives.override

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.directives.OVERRIDE_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import com.expediagroup.graphql.generator.federation.directives.OverrideDirective
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class OverrideDirectiveTest {

    @Test
    fun `verify override directive definition`() {
        val expectedSchema =
            """
            schema @link(import : ["@override"], url : "https://specs.apollo.dev/federation/v2.7"){
              query: Query
            }

            "Marks the field, argument, input field or enum value as deprecated"
            directive @deprecated(
                "The reason for the deprecation"
                reason: String = "No longer supported"
              ) on FIELD_DEFINITION | ARGUMENT_DEFINITION | ENUM_VALUE | INPUT_FIELD_DEFINITION

            "Directs the executor to include this field or fragment only when the `if` argument is true"
            directive @include(
                "Included when true."
                if: Boolean!
              ) on FIELD | FRAGMENT_SPREAD | INLINE_FRAGMENT

            "Links definitions within the document to external schemas."
            directive @link(as: String, import: [link__Import], url: String!) repeatable on SCHEMA

            "Indicates an Input Object is a OneOf Input Object."
            directive @oneOf on INPUT_OBJECT

            "Overrides fields resolution logic from other subgraph. Used for migrating fields from one subgraph to another."
            directive @override(
                "Name of the subgraph to override field resolution"
                from: String!,
                "The value must follow the format of 'percent(number)'. Enterprise feature available in Federation 2.7+."
                label: String
              ) on FIELD_DEFINITION

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
              bar: String! @override(from : "products", label : "percent(50)")
              foo: String! @override(from : "products")
            }

            type _Service {
              sdl: String!
            }

            scalar link__Import
            """.trimIndent()

        val config = FederatedSchemaGeneratorConfig(
            supportedPackages = listOf("com.expediagroup.graphql.generator.federation.directives.override"),
            hooks = FederatedSchemaGeneratorHooks(emptyList())
        )
        val schema = toFederatedSchema(config, listOf(TopLevelObject(Query())))
        Assertions.assertEquals(expectedSchema, schema.print().trim())

        val query = schema.getObjectType("Query")
        assertNotNull(query)
        val fooQuery = query.getField("foo")
        assertNotNull(fooQuery)
        val barQuery = query.getField("bar")
        assertNotNull(barQuery)
        assertNotNull(fooQuery.hasAppliedDirective(OVERRIDE_DIRECTIVE_NAME))
        assertNotNull(barQuery.hasAppliedDirective(OVERRIDE_DIRECTIVE_NAME))
    }

    class Query {
        @OverrideDirective(from = "products")
        fun foo(): String = "test"

        @OverrideDirective(from = "products", label = "percent(50)")
        fun bar(): String = "test"
    }

}
