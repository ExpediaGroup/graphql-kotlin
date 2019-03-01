package com.expedia.graphql.test.integration

import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.testSchemaConfig
import com.expedia.graphql.toSchema
import graphql.GraphQL
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class OptionalResultsTest {
    @Test
    fun `SchemaGenerator generates a simple GraphQL schema`() {
        val schema = toSchema(
            queries = listOf(TopLevelObject(QueryObject())),
            mutations = listOf(),
            config = testSchemaConfig
        )
        val graphQL = GraphQL.newGraphQL(schema).build()

        val result = graphQL.execute("{optionalResults{required optional}}")
        val data: Map<String, Map<String, Any>>? = result.getData()

        val optionalResults = data?.get("optionalResults")
        assertNotNull(optionalResults)
        assertEquals(2, optionalResults.size)
        assertEquals("req", optionalResults["required"])
        assertNull(optionalResults["optional"])
    }
}

data class HasOptionalData(
    val required: String,
    val optional: String?
)

class QueryObject {
    fun optionalResults() = HasOptionalData("req", null)
}
