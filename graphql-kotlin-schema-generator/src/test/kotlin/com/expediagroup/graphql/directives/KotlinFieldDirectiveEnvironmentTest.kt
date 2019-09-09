package com.expediagroup.graphql.directives

import graphql.schema.DataFetcher
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

internal class KotlinFieldDirectiveEnvironmentTest {

    @Test
    fun `getDataFetcher calls the code registry with the coordinates and element`() {
        val coordinates: FieldCoordinates = mockk()
        val codeRegistry: GraphQLCodeRegistry.Builder = mockk()
        every { codeRegistry.getDataFetcher(any<FieldCoordinates>(), any()) } returns mockk()
        val environment = KotlinFieldDirectiveEnvironment(mockk(), mockk(), coordinates, codeRegistry)

        environment.getDataFetcher()

        verify(exactly = 1) { codeRegistry.getDataFetcher(eq(coordinates), any()) }
    }

    @Test
    fun `setDataFetcher sets the new dataFetcher at the coordinates`() {
        val coordinates: FieldCoordinates = mockk()
        val codeRegistry: GraphQLCodeRegistry.Builder = mockk()
        every { codeRegistry.dataFetcher(any(), any<DataFetcher<Any>>()) } returns mockk()
        val environment = KotlinFieldDirectiveEnvironment(mockk(), mockk(), coordinates, codeRegistry)

        val newDataFetcher: DataFetcher<Any> = mockk()
        environment.setDataFetcher(newDataFetcher)

        verify(exactly = 1) { codeRegistry.dataFetcher(eq(coordinates), eq(newDataFetcher)) }
    }
}
