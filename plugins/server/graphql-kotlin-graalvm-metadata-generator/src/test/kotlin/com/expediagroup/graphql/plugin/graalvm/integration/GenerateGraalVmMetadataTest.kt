/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.plugin.graalvm.integration

import com.expediagroup.graphql.plugin.graalvm.generateGraalVmMetadata
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.exists

class GenerateGraalVmMetadataTest {

    @Test
    fun `verifies GraalVm metadata can be generated`(@TempDir tmpDir: Path) {
        val targetDir = tmpDir.toFile()
        generateGraalVmMetadata(targetDirectory = targetDir, supportedPackages = listOf("com.expediagroup.graphql.plugin.graalvm.integration"), mainClassName = "com.example.ApplicationKt")

        Assertions.assertTrue(targetDir.exists())
        Assertions.assertTrue(tmpDir.resolve("native-image.properties").exists())
        Assertions.assertTrue(tmpDir.resolve("reflect-config.json").exists())
        Assertions.assertTrue(tmpDir.resolve("resource-config.json").exists())
    }
}
