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

package com.expediagroup.graphql.examples.server.spring.instrumentation

import com.expediagroup.graphql.examples.server.spring.directives.TRACK_TIMES_INVOKED_DIRECTIVE_NAME
import graphql.ExecutionResult
import graphql.execution.instrumentation.InstrumentationContext
import graphql.execution.instrumentation.InstrumentationState
import graphql.execution.instrumentation.SimpleInstrumentationContext
import graphql.execution.instrumentation.SimplePerformantInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * Adds field count tracking in the instrumentation layer if [com.expediagroup.graphql.examples.directives.TrackTimesInvoked] is present.
 */
@Component
class TrackTimesInvokedInstrumentation : SimplePerformantInstrumentation() {

    private val logger = LoggerFactory.getLogger(TrackTimesInvokedInstrumentation::class.java)

    override fun createState(): InstrumentationState = TrackTimesInvokedInstrumenationState()

    override fun beginFieldFetch(parameters: InstrumentationFieldFetchParameters, state: InstrumentationState?): InstrumentationContext<Any> {
        if (parameters.field.getDirective(TRACK_TIMES_INVOKED_DIRECTIVE_NAME) != null) {
            (state as? TrackTimesInvokedInstrumenationState)?.incrementCount(parameters.field.name)
        }

        return SimpleInstrumentationContext<Any>()
    }

    override fun instrumentExecutionResult(executionResult: ExecutionResult, parameters: InstrumentationExecutionParameters, state: InstrumentationState?): CompletableFuture<ExecutionResult> {
        val count = (state as? TrackTimesInvokedInstrumenationState)?.getCount()
        logger.info("Fields invoked: $count")
        return super.instrumentExecutionResult(executionResult, parameters, state)
    }

    /**
     * The state per execution for this Instrumentation
     */
    private class TrackTimesInvokedInstrumenationState : InstrumentationState {

        private val fieldCount = ConcurrentHashMap<String, Int>()

        fun incrementCount(fieldName: String) {
            val currentCount = fieldCount.getOrDefault(fieldName, 0)
            fieldCount[fieldName] = currentCount.plus(1)
        }

        fun getCount() = fieldCount.toString()
    }
}
