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

package com.expediagroup.graphql.plugin.client.generator

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.io.File

class GenerateSharedResponseTypesIT {

    @ParameterizedTest
    @MethodSource("sharedResponseTypesTests")
    fun `verify generation of client code using shared response types`(testDirectory: File) {
        val config = defaultConfig.copy(
            useSharedResponseTypes = true
        )
        verifyClientGeneration(config, testDirectory)
    }

    companion object {
        @JvmStatic
        fun sharedResponseTypesTests(): List<Arguments> = locateTestCaseArguments("src/test/data/generator/shared_response_types")
    }
}
