/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.types.utils

import com.expediagroup.graphql.generator.annotations.GraphQLValidObjectLocations
import com.expediagroup.graphql.generator.annotations.GraphQLValidObjectLocations.Locations
import com.expediagroup.graphql.generator.exceptions.InvalidObjectLocationException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertFailsWith

class ValidateObjectLocationKtTest {

    class SimpleClass

    @GraphQLValidObjectLocations([Locations.INPUT_OBJECT, Locations.OBJECT])
    class InputAndOutput

    @GraphQLValidObjectLocations([Locations.INPUT_OBJECT])
    class InputOnly

    @GraphQLValidObjectLocations([Locations.OBJECT])
    class OutputOnly

    @Test
    fun `does nothing on class missing annotation`() {
        assertDoesNotThrow {
            validateObjectLocation(SimpleClass::class, Locations.INPUT_OBJECT)
            validateObjectLocation(SimpleClass::class, Locations.OBJECT)
        }
    }

    @Test
    fun `allows all locations on class with all locations`() {
        assertDoesNotThrow {
            validateObjectLocation(InputAndOutput::class, Locations.INPUT_OBJECT)
            validateObjectLocation(InputAndOutput::class, Locations.OBJECT)
        }
    }

    @Test
    fun `validates input only classes`() {
        assertDoesNotThrow {
            validateObjectLocation(InputOnly::class, Locations.INPUT_OBJECT)
        }
        assertFailsWith(InvalidObjectLocationException::class) {
            validateObjectLocation(InputOnly::class, Locations.OBJECT)
        }
    }

    @Test
    fun `validates output only classes`() {
        assertDoesNotThrow {
            validateObjectLocation(OutputOnly::class, Locations.OBJECT)
        }
        assertFailsWith(InvalidObjectLocationException::class) {
            validateObjectLocation(OutputOnly::class, Locations.INPUT_OBJECT)
        }
    }
}
