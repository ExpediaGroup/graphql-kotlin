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

package com.expediagroup.graphql.test.integration

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.createNewTestGenerator
import com.expediagroup.graphql.exceptions.InvalidInterfaceException
import org.junit.jupiter.api.Test
import kotlin.test.assertFailsWith

class InterfaceOfInterfaceTest {

    @Test
    fun `interface of interface`() {
        val queries = listOf(TopLevelObject(InterfaceOfInterfaceQuery()))

        assertFailsWith(InvalidInterfaceException::class) {
            createNewTestGenerator().generateSchema(queries = queries)
        }
    }

    interface FirstLevel {
        val id: String
    }

    interface SecondLevel : FirstLevel {
        val name: String
    }

    class MyClass(override val id: String, override val name: String) : SecondLevel

    class InterfaceOfInterfaceQuery {
        fun getClass() = MyClass(id = "1", name = "fooBar")
    }
}
