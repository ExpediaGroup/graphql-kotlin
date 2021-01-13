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

package com.expediagroup.graphql.generator

import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveWiring
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import io.mockk.every
import io.mockk.spyk

val defaultSupportedPackages = listOf("com.expediagroup.graphql.generator")
val testSchemaConfig = SchemaGeneratorConfig(defaultSupportedPackages)

fun getTestSchemaConfigWithHooks(hooks: SchemaGeneratorHooks) = SchemaGeneratorConfig(defaultSupportedPackages, hooks = hooks)

fun getTestSchemaConfigWithMockedDirectives() = getTestSchemaConfigWithHooks(
    object : SchemaGeneratorHooks {
        override val wiringFactory: KotlinDirectiveWiringFactory
            get() = spyk(KotlinDirectiveWiringFactory()) {
                every { getSchemaDirectiveWiring(any()) } returns object : KotlinSchemaDirectiveWiring {}
            }
    }
)
