package com.expediagroup.graphql.federation.types

import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

internal class ServiceTest {

    @Test
    fun `service object should have the correct naming`() {
        val service = _Service("mySdl")
        assertNotNull(service.sdl)

        val serviceField = SERVICE_FIELD_DEFINITION
        assertEquals(expected = "_service", actual = serviceField.name)

        val serviceObject = serviceField.type as? GraphQLObjectType
        assertNotNull(serviceObject)
        assertEquals(expected = "_Service", actual = serviceObject.name)
        assertEquals(expected = 1, actual = serviceObject.fieldDefinitions.size)
        assertEquals(expected = "sdl", actual = serviceObject.fieldDefinitions.first().name)

        val fieldType = serviceObject.fieldDefinitions.first().type as? GraphQLNonNull
        assertNotNull(fieldType)
        assertEquals(expected = "String", actual = fieldType.wrappedType.name)
    }
}
