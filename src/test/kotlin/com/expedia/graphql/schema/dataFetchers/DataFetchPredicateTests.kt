package com.expedia.graphql.schema.dataFetchers

import com.expedia.graphql.TopLevelObjectDef
import com.expedia.graphql.schema.generator.SchemaGeneratorTest
import com.expedia.graphql.schema.getTestSchemaConfigWithHooks
import com.expedia.graphql.schema.hooks.DataFetcherExecutionPredicate
import com.expedia.graphql.schema.hooks.NoopSchemaGeneratorHooks
import com.expedia.graphql.toSchema
import graphql.ExceptionWhileDataFetching
import graphql.GraphQL
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DataFetchPredicateTests {

    @Test
    fun `A datafetcher execution is stopped if the predicate test is false`() {
        val config = getTestSchemaConfigWithHooks(PredicateHooks())
        val schema = toSchema(
                listOf(TopLevelObjectDef(SchemaGeneratorTest.QueryObject())),
                listOf(TopLevelObjectDef(SchemaGeneratorTest.MutationObject())),
                config = config
        )
        val graphQL = GraphQL.newGraphQL(schema).build()
        val result = graphQL.execute(" { query(value: 1) { id } }")
        val graphQLError = result.errors[0]

        assertEquals("DataFetchingException", graphQLError.errorType.name)
        val exception = (graphQLError as? ExceptionWhileDataFetching)?.exception
        assertEquals("The datafetcher cannot be executed", exception?.message)
        assertTrue(exception is java.lang.IllegalArgumentException)
    }
}

class PredicateHooks : NoopSchemaGeneratorHooks() {
    override val dataFetcherExecutionPredicate: DataFetcherExecutionPredicate? = TestDataFetcherPredicate()
}

class TestDataFetcherPredicate : DataFetcherExecutionPredicate {
    override fun <T> test(value: T): Boolean = false

    override fun onFailure(name: String, klazz: Class<*>): Nothing {
        throw IllegalArgumentException("The datafetcher cannot be executed")
    }
}
