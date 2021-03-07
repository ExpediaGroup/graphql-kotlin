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

package com.expediagroup.graphql.examples.client.maven

import com.expediagroup.graphql.client.spring.GraphQLWebClient
import com.expediagroup.graphql.generated.AddObjectMutation
import com.expediagroup.graphql.generated.ExampleQuery
import com.expediagroup.graphql.generated.HelloWorldQuery
import com.expediagroup.graphql.generated.RetrieveObjectQuery
import com.expediagroup.graphql.generated.UpdateObjectMutation
import io.netty.channel.ChannelOption
import kotlinx.coroutines.runBlocking
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

fun main() {
    val httpClient: HttpClient = HttpClient.create()
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
        .responseTimeout(Duration.ofMillis(60_000))
    val connector: ClientHttpConnector = ReactorClientHttpConnector(httpClient.wiretap(true))
    val webClientBuilder = WebClient.builder()
        .clientConnector(connector)

    val client = GraphQLWebClient(
        url = "http://localhost:8080/graphql",
        builder = webClientBuilder
    )

    println("HelloWorld examples")
    runBlocking {
        val results = client.execute(
            listOf(
                HelloWorldQuery(variables = HelloWorldQuery.Variables(name = null)),
                HelloWorldQuery(variables = HelloWorldQuery.Variables(name = "Dariusz"))
            )
        )

        val resultsNoParam = results[0].data as? HelloWorldQuery.Result
        val resultsWithParam = results[1].data as? HelloWorldQuery.Result
        println("\tquery without parameters result: ${resultsNoParam?.helloWorld}")
        println("\tquery with parameters result: ${resultsWithParam?.helloWorld}")
    }

    // mutation examples
    println("simple mutation examples")
    runBlocking {
        val retrieveNonExistentObject = client.execute(RetrieveObjectQuery(variables = RetrieveObjectQuery.Variables(id = 1)))
        println("\tretrieve non existent object: ${retrieveNonExistentObject.data?.retrieveBasicObject}")

        val addResult = client.execute(AddObjectMutation(variables = AddObjectMutation.Variables(newObject = AddObjectMutation.BasicObjectInput(1, "first"))))
        println("\tadd new object: ${addResult.data?.addBasicObject}")

        val updateResult = client.execute(UpdateObjectMutation(variables = UpdateObjectMutation.Variables(updatedObject = UpdateObjectMutation.BasicObjectInput(1, "updated"))))
        println("\tupdate new object: ${updateResult.data?.updateBasicObject}")
    }

    println("additional examples")
    runBlocking {
        val exampleData = client.execute(ExampleQuery(variables = ExampleQuery.Variables(simpleCriteria = ExampleQuery.SimpleArgumentInput(max = 1.0f))))
        println("\tretrieved interface: ${exampleData.data?.interfaceQuery} ")
        println("\tretrieved union: ${exampleData.data?.unionQuery} ")
        println("\tretrieved enum: ${exampleData.data?.enumQuery} ")
        println("\tretrieved example list: [${exampleData.data?.listQuery?.joinToString { it.name }}]")
    }
}
