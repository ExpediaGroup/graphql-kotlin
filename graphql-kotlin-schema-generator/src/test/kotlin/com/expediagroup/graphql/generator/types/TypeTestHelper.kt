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

package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelNames
import com.expediagroup.graphql.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.directives.KotlinSchemaDirectiveWiring
import com.expediagroup.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.execution.SimpleKotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.ClassScanner
import com.expediagroup.graphql.generator.state.SchemaGeneratorState
import com.expediagroup.graphql.generator.state.TypesCache
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
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
internal open class TypeTestHelper {
    private val supportedPackages = listOf("com.expediagroup.graphql")
    private val classScanner = ClassScanner(supportedPackages)
    private val dataFetcherFactory: KotlinDataFetcherFactoryProvider = SimpleKotlinDataFetcherFactoryProvider()
    private val topLevelNames = TopLevelNames(
        query = "TestTopLevelQuery",
        mutation = "TestTopLevelMutation",
        subscription = "TestTopLevelSubscription"
    )
    private val state = spyk(SchemaGeneratorState(supportedPackages))
    private val cache = spyk(TypesCache(supportedPackages))
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

        cache.clear()
        every { state.cache } returns cache
        every { config.hooks } returns hooks
        every { config.dataFetcherFactoryProvider } returns dataFetcherFactory
        every { spyWiringFactory.getSchemaDirectiveWiring(any()) } returns object : KotlinSchemaDirectiveWiring {}
        every { config.topLevelNames } returns TopLevelNames(
            query = "TestTopLevelQuery",
            mutation = "TestTopLevelMutation",
            subscription = "TestTopLevelSubscription"
        )

        beforeTest()
    }

    @AfterAll
    fun cleanup() {
        classScanner.close()
    }

    open fun beforeTest() {}

    open fun beforeSetup() {}
}
