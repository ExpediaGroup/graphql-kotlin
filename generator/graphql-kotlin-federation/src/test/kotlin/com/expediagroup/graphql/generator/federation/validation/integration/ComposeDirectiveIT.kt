package com.expediagroup.graphql.generator.federation.validation.integration

import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.federation.data.integration.composeDirective.CustomSchema
import com.expediagroup.graphql.generator.federation.data.integration.composeDirective.SimpleQuery
import com.expediagroup.graphql.generator.federation.toFederatedSchema
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class ComposeDirectiveIT {

    @Test
    fun `verifies applying @composeDirective generates valid schema`() {
        assertDoesNotThrow {
            val schema = toFederatedSchema(
                config = federatedTestConfig("com.expediagroup.graphql.generator.federation.data.integration.composeDirective"),
                queries = listOf(TopLevelObject(SimpleQuery())),
                schemaObject = TopLevelObject(CustomSchema())
            )

            val expected = """
                schema @composeDirective(name : "custom") @link(import : ["@composeDirective", "@extends", "@external", "@inaccessible", "@interfaceObject", "@key", "@override", "@provides", "@requires", "@shareable", "@tag", "FieldSet"], url : "https://specs.apollo.dev/federation/v2.3"){
                  query: Query
                }

                "Marks underlying custom directive to be included in the Supergraph schema"
                directive @composeDirective(name: String!) repeatable on SCHEMA

                directive @custom on FIELD_DEFINITION

                "Links definitions within the document to external schemas."
                directive @link(import: [String], url: String!) repeatable on SCHEMA

                type Query {
                  _service: _Service!
                  helloWorld: String! @custom
                }

                type _Service {
                  sdl: String!
                }
            """.trimIndent()
            val actual = schema.print(
                includeDirectivesFilter = { directive -> "link" == directive || "composeDirective" == directive || "custom" == directive },
                includeScalarTypes = false
            ).trim()
            Assertions.assertEquals(expected, actual)
        }
    }
}
