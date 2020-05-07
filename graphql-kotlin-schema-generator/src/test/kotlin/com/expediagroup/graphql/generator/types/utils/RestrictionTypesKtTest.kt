/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.generator.types.utils

import com.expediagroup.graphql.annotations.GraphQLTypeRestriction
import com.expediagroup.graphql.annotations.GraphQLTypeRestriction.GraphQLType
import com.expediagroup.graphql.exceptions.TypeRestrictionException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertFailsWith

class RestrictionTypesKtTest {

    private class AnyType

    @GraphQLTypeRestriction(GraphQLType.INPUT)
    private class InputOnly

    @GraphQLTypeRestriction(GraphQLType.OUTPUT)
    private class OutputOnly

    @Test
    fun throwIfInvalidRestrictionType() {
        assertDoesNotThrow {
            throwIfInvalidRestrictionType(AnyType::class, GraphQLType.OUTPUT)
            throwIfInvalidRestrictionType(AnyType::class, GraphQLType.INPUT)
            throwIfInvalidRestrictionType(OutputOnly::class, GraphQLType.OUTPUT)
            throwIfInvalidRestrictionType(InputOnly::class, GraphQLType.INPUT)
        }
        assertFailsWith(TypeRestrictionException::class) {
            throwIfInvalidRestrictionType(OutputOnly::class, GraphQLType.INPUT)
        }
        assertFailsWith(TypeRestrictionException::class) {
            throwIfInvalidRestrictionType(InputOnly::class, GraphQLType.OUTPUT)
        }
    }
}
