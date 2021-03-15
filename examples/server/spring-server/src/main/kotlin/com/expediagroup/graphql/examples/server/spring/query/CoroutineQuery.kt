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

package com.expediagroup.graphql.examples.server.spring.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CoroutineQuery : Query {

    private val logger = LoggerFactory.getLogger(CoroutineQuery::class.java)

    @GraphQLDescription("Example query that uses coroutines")
    suspend fun exampleCoroutineQuery(msg: String): String = coroutineScope {
        val start = System.currentTimeMillis()
        val slow = async { slowFunction(msg) }
        val fast = async { fastFunction(msg) }
        val result = "${fast.await()}:${slow.await()}"
        logger.info("computed final result in ${System.currentTimeMillis() - start} ms")
        result
    }

    suspend fun slowFunction(msg: String): String {
        val slowFunctionStart = System.currentTimeMillis()
        delay(1000L)
        val slowResult = msg.reversed()
        logger.info("CoroutineQuery.slowFunction - computed slow result in ${System.currentTimeMillis() - slowFunctionStart} ms")
        return slowResult
    }

    suspend fun fastFunction(msg: String): String {
        val fastFunctionStart = System.currentTimeMillis()
        delay(200L)
        logger.info("CoroutineQuery.fastFunction - computed fast result in ${System.currentTimeMillis() - fastFunctionStart} ms")
        return msg
    }
}
