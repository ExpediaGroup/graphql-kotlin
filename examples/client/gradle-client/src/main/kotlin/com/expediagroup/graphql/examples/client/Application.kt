package com.expediagroup.graphql.examples.client

import com.expediagroup.graphql.client.GraphQLClient
import com.expediagroup.graphql.generated.AddObjectMutation
import com.expediagroup.graphql.generated.HelloWorldQuery
import com.expediagroup.graphql.generated.RetrieveObjectQuery
import com.expediagroup.graphql.generated.UpdateObjectMutation
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.logging.DEFAULT
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import kotlinx.coroutines.runBlocking
import java.net.URL
import java.util.concurrent.TimeUnit

fun main() {
    val jackson = jacksonObjectMapper()
    val client = GraphQLClient(
            url = URL("http://localhost:8080/graphql"),
            engineFactory = OkHttp,
            mapper = jackson
    ) {
        engine {
            config {
                connectTimeout(10, TimeUnit.SECONDS)
                readTimeout(60, TimeUnit.SECONDS)
                writeTimeout(60, TimeUnit.SECONDS)
            }
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.HEADERS
        }
    }
    val helloWorldQuery = HelloWorldQuery(client)
    println("HelloWorld examples")
    runBlocking {
        val helloWorldResultNoParam = helloWorldQuery.execute(variables = HelloWorldQuery.Variables(name = null))
        println("\tquery without parameters result: ${helloWorldResultNoParam.data?.helloWorld}")

        val helloWorldResult = helloWorldQuery.execute(variables = HelloWorldQuery.Variables(name = "Dariusz"))
        println("\tquery with parameters result: ${helloWorldResult.data?.helloWorld}")
    }

    // mutation examples
    println("simple mutation examples")
    val retrieveObjectQuery = RetrieveObjectQuery(client)
    val addObjectMutation = AddObjectMutation(client)
    val updateObjectMutation = UpdateObjectMutation(client)
    runBlocking {
        val retrieveNonExistentObject = retrieveObjectQuery.execute(variables = RetrieveObjectQuery.Variables(id = 1))
        println("\tretrieve non existent object: ${retrieveNonExistentObject.data?.retrieveBasicObject}")

        val addResult = addObjectMutation.execute(variables = AddObjectMutation.Variables(newObject = AddObjectMutation.BasicObjectInput(1, "first")))
        println("\tadd new object: ${addResult.data?.addBasicObject}")

        val updateResult = updateObjectMutation.execute(variables = UpdateObjectMutation.Variables(updatedObject = UpdateObjectMutation.BasicObjectInput(1, "updated")))
        println("\tupdate new object: ${updateResult.data?.updateBasicObject}")
    }
    client.close()
}
