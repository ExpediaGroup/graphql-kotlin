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

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelNames
import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.directives.KotlinSchemaDirectiveWiring
import com.expediagroup.graphql.generator.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.execution.SimpleKotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import io.mockk.every
import io.mockk.spyk
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle

@Suppress(
    "Detekt.UnsafeCast",
    "Detekt.UnsafeCallOnNullableType",
    "Detekt.LongMethod"
)
@TestInstance(Lifecycle.PER_CLASS)
open class TypeTestHelper {
    private val supportedPackages = listOf("com.expediagroup.graphql.generator")
    private val dataFetcherFactory: KotlinDataFetcherFactoryProvider = SimpleKotlinDataFetcherFactoryProvider()
    private val topLevelNames = TopLevelNames(
        query = "TestTopLevelQuery",
        mutation = "TestTopLevelMutation",
        subscription = "TestTopLevelSubscription"
    )

    val spyWiringFactory = spyk(KotlinDirectiveWiringFactory())
    var hooks: SchemaGeneratorHooks = object : SchemaGeneratorHooks {
        override val wiringFactory: KotlinDirectiveWiringFactory
            get() = spyWiringFactory
    }
    val config = spyk(SchemaGeneratorConfig(supportedPackages, topLevelNames, hooks, dataFetcherFactory))
    val generator = spyk(SchemaGenerator(config))

    @BeforeEach
    fun setup() {
        beforeSetup()

        generator.additionalTypes.clear()
        generator.directives.clear()
        generator.cache.close()

        every { config.hooks } returns hooks
        every { config.dataFetcherFactoryProvider } returns dataFetcherFactory
        every { spyWiringFactory.getSchemaDirectiveWiring(any()) } returns object : KotlinSchemaDirectiveWiring {}

        beforeTest()
    }

    @AfterAll
    fun cleanup() {
        generator.close()
    }

    open fun beforeTest() {}

    open fun beforeSetup() {}
}
