package com.expediagroup.scalars

import com.expediagroup.graphql.client.jackson.types.OptionalInput
import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.scalars.generated.CustomScalarQuery
import com.expediagroup.scalars.generated.inputs.ScalarWrapperInput
import com.ibm.icu.util.ULocale
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import java.util.UUID
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomScalarApplicationTests(@LocalServerPort private val port: Int) {

    @Test
    fun `verify custom scalars are correctly serialized and deserialized`() = runBlocking {
        val client = GraphQLWebClient(url = "http://localhost:$port/graphql")

        val wrapperInput = ScalarWrapperInput(
            id = "123456789abcdef",
            locale = ULocale.US,
            localeList = listOf(ULocale.FRANCE, ULocale.UK),
            name = "junit_test",
            rating = OptionalInput.Defined(1.2345),
            uuid = OptionalInput.Defined(UUID.randomUUID()),
            uuidList = OptionalInput.Defined(listOf(UUID.randomUUID())),
            valid = true
        )

        val query = CustomScalarQuery(variables = CustomScalarQuery.Variables(
            required = ULocale.US,
            optional = OptionalInput.Undefined,
            wrapper = OptionalInput.Defined(wrapperInput)
        ))

        val response = client.execute(query)
        val wrapperResponse = response.data?.scalarQuery
        assertNotNull(wrapperResponse)

        assertNull(wrapperResponse.count)
        assertEquals(wrapperInput.id, wrapperResponse.id)
        assertEquals(wrapperInput.localeList, wrapperResponse.localeList)
        assertEquals(wrapperInput.name, wrapperResponse.name)
        // default value from server
        assertEquals("undefined value", wrapperResponse.optional)
        assertEquals((wrapperInput.rating as? OptionalInput.Defined<Double>)?.value, wrapperResponse.rating)
        assertEquals((wrapperInput.uuid as? OptionalInput.Defined<UUID>)?.value, wrapperResponse.uuid)
        assertEquals((wrapperInput.uuidList as? OptionalInput.Defined<List<UUID>>)?.value, wrapperResponse.uuidList)
        assertEquals(wrapperInput.valid, wrapperResponse.valid)
    }

}
