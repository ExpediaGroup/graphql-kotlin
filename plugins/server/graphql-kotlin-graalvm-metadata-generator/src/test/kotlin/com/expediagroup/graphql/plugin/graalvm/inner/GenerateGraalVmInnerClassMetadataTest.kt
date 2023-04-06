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

package com.expediagroup.graphql.plugin.graalvm.inner

import com.expediagroup.graphql.plugin.graalvm.ClassMetadata
import com.expediagroup.graphql.plugin.graalvm.MethodMetadata
import com.expediagroup.graphql.plugin.graalvm.generateGraalVmReflectMetadata
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GenerateGraalVmInnerClassMetadataTest {

    @Test
    fun `verifies we can generate valid reflect metadata for query referencing inner class`() {
        val expected = listOf(
            ClassMetadata(
                name = "com.expediagroup.graphql.plugin.graalvm.inner.InnerClassQuery",
                methods = listOf(
                    MethodMetadata(
                        name = "innerClassArg",
                        parameterTypes = listOf("com.expediagroup.graphql.plugin.graalvm.inner.InnerClassQuery\$InnerClass")
                    ),
                    MethodMetadata(
                        name = "innerClassQuery",
                        parameterTypes = listOf()
                    )
                )
            ),
            ClassMetadata(
                name = "com.expediagroup.graphql.plugin.graalvm.inner.InnerClassQuery\$InnerClass",
                allDeclaredFields = true,
                allPublicConstructors = true,
                methods = listOf(
                    MethodMetadata(
                        name = "getProp",
                        parameterTypes = listOf()
                    )
                )
            )
        )

        val actual = generateGraalVmReflectMetadata(supportedPackages = listOf("com.expediagroup.graphql.plugin.graalvm.inner"))

        val mapper = jacksonObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        val writer = mapper.writerWithDefaultPrettyPrinter()
        Assertions.assertEquals(writer.writeValueAsString(expected), writer.writeValueAsString(actual))
    }
}
