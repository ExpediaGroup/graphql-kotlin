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

package com.expediagroup.graphql.federation.tracing

import com.google.protobuf.Timestamp
import graphql.execution.ExecutionPath
import graphql.execution.instrumentation.InstrumentationState
import graphql.execution.instrumentation.SimpleInstrumentation
import graphql.execution.instrumentation.parameters.InstrumentationCreateStateParameters

import mdg.engine.proto.Reports
import java.time.Instant

class FederatedTracingInstrumentation : SimpleInstrumentation() {

    override fun createState(parameters: InstrumentationCreateStateParameters): InstrumentationState? {
        val context = parameters.executionInput.context
        if (context is HttpRequestHeaders) {
            val headerValue = context.getHTTPRequestHeader(HEADER_NAME)
            if (headerValue == null || headerValue != HEADER_VALUE) {
                return null
            }
        }
        return FederatedTracingState()
    }

    companion object {
        const val HEADER_NAME: String = "apollo-federation-include-trace"
        const val HEADER_VALUE: String = "ftv1"
    }

    // TODO give necessary credit to federation-jvm
    private class FederatedTracingState : InstrumentationState {
        private var startRequestTime: Instant = Instant.now()
        private var startRequestNanos: Long = System.nanoTime()
        private var nodesByPath: LinkedHashMap<ExecutionPath, Reports.Trace.Node.Builder> = java.util.LinkedHashMap()

        init {
            nodesByPath[ExecutionPath.rootPath()] = Reports.Trace.Node.newBuilder()
        }

        fun toProto(): Reports.Trace = Reports.Trace.newBuilder()
            .setStartTime(getStartTimestamp())
            .setEndTime(getNowTimestamp())
            .setDurationNs(getDuration())
            .setRoot(nodesByPath[ExecutionPath.rootPath()])
            .build()

        private fun getStartTimestamp(): Timestamp = instantToTimestamp(startRequestTime)

        private fun getNowTimestamp(): Timestamp = instantToTimestamp(Instant.now())

        private fun instantToTimestamp(startRequestTime: Instant): Timestamp = Timestamp.newBuilder()
            .setSeconds(startRequestTime.epochSecond)
            .setNanos(startRequestTime.nano).build()

        private fun getDuration(): Long = System.nanoTime() - startRequestNanos
    }
}
