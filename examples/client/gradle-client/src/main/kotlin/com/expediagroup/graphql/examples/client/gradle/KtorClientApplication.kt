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

package com.expediagroup.graphql.examples.client.gradle

import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import com.expediagroup.graphql.client.types.GraphQLClientResponse
import com.expediagroup.graphql.generated.AddObjectMutation
import com.expediagroup.graphql.generated.EntitiesQuery
import com.expediagroup.graphql.generated.ExampleQuery
import com.expediagroup.graphql.generated.HelloWorldQuery
import com.expediagroup.graphql.generated.RetrieveObjectQuery
import com.expediagroup.graphql.generated.UpdateObjectMutation
import com.expediagroup.graphql.generated.entitiesquery.Product
import com.expediagroup.graphql.generated.inputs.BasicObjectInput
import com.expediagroup.graphql.generated.inputs.SimpleArgumentInput
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.net.URL
import java.util.concurrent.TimeUnit

fun main() {
    /*
     * ************************************************************
     * Make sure to start example server before running this code
     * https://github.com/dariuszkuc/graphql-kotlin/blob/client-custom-scalars/examples/client/server/src/main/kotlin/com/expediagroup/graphql/examples/client/server/Application.kt
     * ************************************************************
     */
    val httpClient = HttpClient(engineFactory = OkHttp) {
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
    val client = GraphQLKtorClient(
        url = URL("http://localhost:8080/graphql"),
        httpClient = httpClient
    )
    println("HelloWorld examples")
    runBlocking {
        val helloWorldQuery = HelloWorldQuery(variables = HelloWorldQuery.Variables())
        val helloWorldResult = client.execute(helloWorldQuery)
        println("\tquery without parameters result: ${helloWorldResult.data?.helloWorld}")

        val helloWorldResultImplicit: GraphQLClientResponse<HelloWorldQuery.Result> = client.execute(helloWorldQuery)

        val results = client.execute(
            listOf(
                HelloWorldQuery(variables = HelloWorldQuery.Variables(name = null)),
                HelloWorldQuery(variables = HelloWorldQuery.Variables(name = "Dariusz"))
            )
        )

        val resultsNoParam = results[0].data as? HelloWorldQuery.Result
        val resultsWithParam = results[1].data as? HelloWorldQuery.Result
        println("\tquery with null name result: ${resultsNoParam?.helloWorld}")
        println("\tquery with parameters result: ${resultsWithParam?.helloWorld}")
    }

    // mutation examples
    println("simple mutation examples")
    runBlocking {
        val retrieveNonExistentObject = client.execute(RetrieveObjectQuery(variables = RetrieveObjectQuery.Variables(id = 1)))
        println("\tretrieve non existent object: ${retrieveNonExistentObject.data?.retrieveBasicObject}")

        val addResult = client.execute(AddObjectMutation(variables = AddObjectMutation.Variables(newObject = BasicObjectInput(1, "first"))))
        println("\tadd new object: ${addResult.data?.addBasicObject}")

        val updateResult = client.execute(UpdateObjectMutation(variables = UpdateObjectMutation.Variables(updatedObject = BasicObjectInput(1, "updated"))))
        println("\tupdate new object: ${updateResult.data?.updateBasicObject}")
    }

    println("additional examples")
    runBlocking {
        val exampleData = client.execute(ExampleQuery(variables = ExampleQuery.Variables(simpleCriteria = SimpleArgumentInput(max = 1.0))))
        println("\tretrieved interface: ${exampleData.data?.interfaceQuery} ")
        println("\tretrieved union: ${exampleData.data?.unionQuery} ")
        println("\tretrieved enum: ${exampleData.data?.enumQuery} ")
        println("\tretrieved scalar: ${exampleData.data?.scalarQuery}")
        println("\tretrieved example list: [${exampleData.data?.listQuery?.joinToString { it.name }}]")
    }

    println("entities query")
    runBlocking {
        val entity = Json.decodeFromString<JsonObject>(
            """
            |{
            |  "__typename": "Product",
            |  "id": "apollo-federation"
            |}
            """.trimMargin()
        )
        val entityData = client.execute(EntitiesQuery(variables = EntitiesQuery.Variables(representations = listOf(entity))))
        val product = entityData.data?._entities?.get(0) as? Product
        println("\tretrieved product SKU: ${product?.sku}")
        println("\tretrieved product package: ${product?.`package`}")
        println("\tretrieved product variation ID: ${product?.variation?.id}")
        println("\tretrieved product dimensions: size=${product?.dimensions?.size}, weight=${product?.dimensions?.weight}")
    }

    client.close()
}
