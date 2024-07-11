/*
 * Copyright 2024 Expedia, Inc
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

package com.expediagroup.graphql.dataloader.instrumentation.level.execution

import graphql.execution.ExecutionContext

/**
 * Source of level dispatched state calculation
 */
enum class ExecutionLevelCalculationSource { EXECUTION_STRATEGY, FIELD_FETCH }

/**
 * Hold information related to from where the level dispatched state is being calculated
 */
data class ExecutionLevelDispatchedInstrumentationParameters(
    val executionContext: ExecutionContext,
    val calculationSource: ExecutionLevelCalculationSource
)
