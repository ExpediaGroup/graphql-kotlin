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

package com.expediagroup.graphql.plugin.graalvm.boxed

import com.expediagroup.graphql.plugin.graalvm.ClassMetadata
import com.expediagroup.graphql.plugin.graalvm.MethodMetadata
import com.expediagroup.graphql.plugin.graalvm.generateGraalVmReflectMetadata
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class GenerateGraalVmBoxedMetadataTest {

    @Test
    fun `verifies we can generate valid reflect metadata for query with boxed primitives`() {
        val expected = listOf(
            ClassMetadata(
                name = "com.expediagroup.graphql.plugin.graalvm.boxed.BoxedArgQuery",
                methods = listOf(
                    MethodMetadata(
                        name = "optionalBooleanArg",
                        parameterTypes = listOf("java.lang.Boolean")
                    ),
                    MethodMetadata(
                        name = "optionalBooleanArg\$default",
                        parameterTypes = listOf("com.expediagroup.graphql.plugin.graalvm.boxed.BoxedArgQuery", "java.lang.Boolean", "int", "java.lang.Object")
                    ),
                    MethodMetadata(
                        name = "optionalDoubleArg",
                        parameterTypes = listOf("java.lang.Double")
                    ),
                    MethodMetadata(
                        name = "optionalDoubleArg\$default",
                        parameterTypes = listOf("com.expediagroup.graphql.plugin.graalvm.boxed.BoxedArgQuery", "java.lang.Double", "int", "java.lang.Object")
                    ),
                    MethodMetadata(
                        name = "optionalIntArg",
                        parameterTypes = listOf("java.lang.Integer")
                    ),
                    MethodMetadata(
                        name = "optionalIntArg\$default",
                        parameterTypes = listOf("com.expediagroup.graphql.plugin.graalvm.boxed.BoxedArgQuery", "java.lang.Integer", "int", "java.lang.Object")
                    ),
                    MethodMetadata(
                        name = "optionalStringArg",
                        parameterTypes = listOf("java.lang.String")
                    ),
                    MethodMetadata(
                        name = "optionalStringArg\$default",
                        parameterTypes = listOf("com.expediagroup.graphql.plugin.graalvm.boxed.BoxedArgQuery", "java.lang.String", "int", "java.lang.Object")
                    )
                )
            )
        )

        val actual = generateGraalVmReflectMetadata(supportedPackages = listOf("com.expediagroup.graphql.plugin.graalvm.boxed"))

        val mapper = jacksonObjectMapper()
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        val writer = mapper.writerWithDefaultPrettyPrinter()
        Assertions.assertEquals(writer.writeValueAsString(expected), writer.writeValueAsString(actual))
    }
}
