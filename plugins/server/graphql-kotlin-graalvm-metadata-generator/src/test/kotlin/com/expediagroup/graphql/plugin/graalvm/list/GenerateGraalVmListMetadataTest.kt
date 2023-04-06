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

package com.expediagroup.graphql.plugin.graalvm.list

import com.expediagroup.graphql.plugin.graalvm.ClassMetadata
import com.expediagroup.graphql.plugin.graalvm.MethodMetadata
import com.expediagroup.graphql.plugin.graalvm.generateGraalVmReflectMetadata
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GenerateGraalVmListMetadataTest {

    @Test
    fun `verifies we can generate valid reflect metadata for query referencing list`() {
        val expected = listOf(
            ClassMetadata(
                name = "com.expediagroup.graphql.plugin.graalvm.list.InputOnly",
                allPublicConstructors = true
            ),
            ClassMetadata(
                name = "com.expediagroup.graphql.plugin.graalvm.list.ListQuery",
                methods = listOf(
                    MethodMetadata(
                        name = "listObjectArg",
                        parameterTypes = listOf("java.util.List")
                    ),
                    MethodMetadata(
                        name = "listObjectQuery",
                        parameterTypes = listOf()
                    ),
                    MethodMetadata(
                        name = "listPrimitiveArg",
                        parameterTypes = listOf("java.util.List")
                    ),
                    MethodMetadata(
                        name = "listQuery",
                        parameterTypes = listOf()
                    ),
                    MethodMetadata(
                        name = "optionalListArg",
                        parameterTypes = listOf("java.util.List")
                    ),
                    MethodMetadata(
                        name = "optionalListArg\$default",
                        parameterTypes = listOf("com.expediagroup.graphql.plugin.graalvm.list.ListQuery", "java.util.List", "int", "java.lang.Object")
                    )
                )
            ),
            ClassMetadata(
                name = "com.expediagroup.graphql.plugin.graalvm.list.OutputOnly",
                allDeclaredFields = true,
                methods = listOf(
                    MethodMetadata(
                        name = "calculate",
                        parameterTypes = listOf()
                    ),
                    MethodMetadata(
                        name = "getDescription",
                        parameterTypes = listOf()
                    ),
                    MethodMetadata(
                        name = "getId",
                        parameterTypes = listOf()
                    )
                )
            )
        )

        val actual = generateGraalVmReflectMetadata(supportedPackages = listOf("com.expediagroup.graphql.plugin.graalvm.list"))

        val mapper = jacksonObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        val writer = mapper.writerWithDefaultPrettyPrinter()
        Assertions.assertEquals(writer.writeValueAsString(expected), writer.writeValueAsString(actual))
    }
}
