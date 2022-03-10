/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.transactionbatcher.instrumentation.execution

import com.expediagroup.graphql.transactionbatcher.instrumentation.state.Level
import graphql.ExecutionInput

/**
 * Defines the contract for the behavior that needs to be executed when a certain event happened
 */
interface ExecutionLevelInstrumentationContext {
    /**
     * this is invoked when all [ExecutionInput] in a GraphQLContext dispatched a certain level.
     *
     * @param level that was dispatched on all [ExecutionInput]
     * @param executions list of executions that just dispatched a certain level
     */
    fun onDispatched(
        level: Level,
        executions: List<ExecutionInput>
    )
}
