package com.expediagroup.graphql.sample.query

import com.expediagroup.graphql.annotations.GraphQLDescription
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CoroutineQuery: Query {

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
