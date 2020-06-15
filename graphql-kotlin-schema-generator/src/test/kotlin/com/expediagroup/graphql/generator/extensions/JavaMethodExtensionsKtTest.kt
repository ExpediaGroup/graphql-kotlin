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

package com.expediagroup.graphql.generator.extensions

import com.expediagroup.graphql.annotations.GraphQLIgnore
import org.junit.jupiter.api.Test
import kotlin.reflect.jvm.javaGetter
import kotlin.test.assertEquals

class JavaMethodExtensionsKtTest {

    class MyTestClass(
        val basicProperty: String,

        @GraphQLIgnore
        val ignoredProperty: String,

        @get:GraphQLIgnore
        val ignoredGetter: String
    )

    @Test
    fun `Java getter isGraphQLIgnored`() {
        assertEquals(false, MyTestClass::basicProperty.javaGetter?.isGraphQLIgnored())
        assertEquals(false, MyTestClass::ignoredProperty.javaGetter?.isGraphQLIgnored())
        assertEquals(true, MyTestClass::ignoredGetter.javaGetter?.isGraphQLIgnored())
    }
}
