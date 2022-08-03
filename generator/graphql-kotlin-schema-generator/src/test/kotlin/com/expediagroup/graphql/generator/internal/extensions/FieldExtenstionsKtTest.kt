/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.extensions

import com.expediagroup.graphql.generator.annotations.GraphQLDeprecated
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FieldExtenstionsKtTest {

    enum class AnnotatedEnum {
        @GraphQLDescription("field description")
        @Deprecated("do not use", ReplaceWith("TWO"))
        @GraphQLName("customOne")
        ONE,
        TWO,
        @GraphQLDeprecated("do not use", ReplaceWith("TWO"))
        THREE
    }

    @Test
    fun `verify @GraphQLDescrption on fields`() {
        val description = AnnotatedEnum::class.java.getField("ONE")
        val noDescription = AnnotatedEnum::class.java.getField("TWO")
        assertEquals(expected = "field description", actual = description.getGraphQLDescription())
        assertNull(noDescription.getGraphQLDescription())
    }

    @Test
    fun `verify @Deprecated on fields`() {
        val propertyDeprecation = AnnotatedEnum::class.java.getField("ONE").getDeprecationReason()
        assertEquals(expected = "do not use, replace with TWO", actual = propertyDeprecation)
    }

    @Test
    fun `verify @GraphQLDeprecated on fields`() {
        val propertyDeprecation = AnnotatedEnum::class.java.getField("THREE").getDeprecationReason()
        assertEquals(expected = "do not use, replace with TWO", actual = propertyDeprecation)
    }

    @Test
    fun `verify @GraphQLName on fields`() {
        val customNameField = AnnotatedEnum::class.java.getField("ONE")
        assertEquals(expected = "customOne", actual = customNameField.getGraphQLName())

        val basicNameField = AnnotatedEnum::class.java.getField("TWO")
        assertEquals(expected = "TWO", actual = basicNameField.getGraphQLName())
    }
}
