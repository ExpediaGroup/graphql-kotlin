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

package com.expediagroup.graphql.plugin.client.generator

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class GenerateJacksonClientIT {

    @ParameterizedTest
    @MethodSource("jacksonTests")
    fun `verify generation of client code using jackson`(testDirectory: File) {
        val config = defaultConfig.copy(
            customScalarMap = mapOf(
                "UUID" to GraphQLScalar("UUID", "java.util.UUID", "com.expediagroup.graphql.plugin.client.generator.UUIDScalarConverter"),
                "Locale" to GraphQLScalar("Locale", "com.ibm.icu.util.ULocale", "com.expediagroup.graphql.plugin.client.generator.ULocaleScalarConverter")
            ),
            serializer = GraphQLSerializer.JACKSON,
            useOptionalInputWrapper = true
        )
        verifyClientGeneration(config, testDirectory)
    }

    companion object {
        @JvmStatic
        fun jacksonTests(): List<Arguments> = locateTestCaseArguments("src/test/data/jackson")
    }
}
