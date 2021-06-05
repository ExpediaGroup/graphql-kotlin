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

package com.expediagroup.graphql.generator.hooks

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ExperimentalReflectionOnLambdas
class FlowSubscriptionSchemaGeneratorHooksTest {

    val hooks = FlowSubscriptionSchemaGeneratorHooks()

    @Test
    fun `willResolveMonad unwraps Flow`() {
        val type = assertNotNull(TestQuery::getFlow.returnType)
        val result = hooks.willResolveMonad(type)
        assertEquals(String::class.createType(), result)
    }

    @Test
    fun `willResolveMonad does nothing on any other type`() {
        val stringType = assertNotNull(TestQuery::getString.returnType)
        val cfType = assertNotNull(TestQuery::getCompletableFuture.returnType)

        assertEquals(stringType, hooks.willResolveMonad(stringType))
        assertEquals(cfType, hooks.willResolveMonad(cfType))
    }

    @Test
    fun isValidSubscriptionReturnType() {
        assertTrue(hooks.isValidSubscriptionReturnType(TestQuery::class, TestQuery::getFlow))
        assertFalse(hooks.isValidSubscriptionReturnType(TestQuery::class, TestQuery::getString))
        assertFalse(hooks.isValidSubscriptionReturnType(TestQuery::class, TestQuery::getCompletableFuture))
    }

    class TestQuery {
        fun getFlow(): Flow<String> = emptyFlow()
        fun getString(): String = ""
        fun getCompletableFuture(): CompletableFuture<String> = CompletableFuture.completedFuture("")
    }
}
