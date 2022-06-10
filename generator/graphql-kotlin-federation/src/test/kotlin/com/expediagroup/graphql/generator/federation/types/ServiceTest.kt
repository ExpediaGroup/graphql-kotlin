/*
 * Copyright 2022 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.generator.federation.types

import graphql.Scalars.GraphQLString
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class ServiceTest {

    @Test
    fun `service object should have the correct naming`() {
        val service = _Service("mySdl")
        assertNotNull(service.sdl)

        val serviceField = SERVICE_FIELD_DEFINITION
        assertEquals(expected = "_service", actual = serviceField.name)

        val wrappedServiceObject = serviceField.type as? GraphQLNonNull
        assertNotNull(wrappedServiceObject)
        val serviceObject = wrappedServiceObject.wrappedType as? GraphQLObjectType
        assertNotNull(serviceObject)
        assertEquals(expected = "_Service", actual = serviceObject.name)
        assertEquals(expected = 1, actual = serviceObject.fieldDefinitions.size)
        assertEquals(expected = "sdl", actual = serviceObject.fieldDefinitions.first().name)

        val fieldType = serviceObject.fieldDefinitions.first().type as? GraphQLNonNull
        assertNotNull(fieldType)
        assertEquals(expected = GraphQLString, actual = fieldType.wrappedType)
    }
}
