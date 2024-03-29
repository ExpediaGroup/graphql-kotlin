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

package com.expediagroup.graphql.plugin.maven

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Paths

class GenerateClientMojoTest {

    @Test
    fun `verify client code was generated`() {
        val buildDirectory = System.getProperty("buildDirectory")
        val queryFilePath = Paths.get(buildDirectory, "generated-sources", "graphql", "com", "expediagroup", "graphql", "plugin", "generated", "ExampleQuery.kt")
        assertTrue(queryFilePath.toFile().exists(), "graphql client query file was generated")
        val typeAliasesFilePath = Paths.get(buildDirectory, "generated-sources", "graphql", "com", "expediagroup", "graphql", "plugin", "generated", "GraphQLTypeAliases.kt")
        assertTrue(typeAliasesFilePath.toFile().exists(), "graphql client type aliases were generated")
        val queryFilePathSubDir = Paths.get(buildDirectory, "generated-sources", "graphql", "com", "expediagroup", "graphql", "plugin", "generated", "SubDirQuery.kt")
        assertTrue(queryFilePathSubDir.toFile().exists(), "graphql client query sub dir file was generated")
    }
}
