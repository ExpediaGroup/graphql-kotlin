package com.expediagroup.graphql.examples.client

import com.expediagroup.graphql.client.GraphQLWebClient
import com.expediagroup.graphql.generated.AddObjectMutation
import com.expediagroup.graphql.generated.HelloWorldQuery
import com.expediagroup.graphql.generated.RetrieveObjectQuery
import com.expediagroup.graphql.generated.UpdateObjectMutation
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import kotlinx.coroutines.runBlocking
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.util.concurrent.TimeUnit

fun main() {
    val httpClient: HttpClient = HttpClient.create()
        .tcpConfiguration { client ->
            client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10_000)
                .doOnConnected { conn ->
                    conn.addHandlerLast(ReadTimeoutHandler(60_000, TimeUnit.MILLISECONDS))
                    conn.addHandlerLast(WriteTimeoutHandler(60_000, TimeUnit.MILLISECONDS))
                }
        }
    val connector: ClientHttpConnector = ReactorClientHttpConnector(httpClient.wiretap(true))
    val webClientBuilder = WebClient.builder()
        .clientConnector(connector)

    val client = GraphQLWebClient(
        url = "http://localhost:8080/graphql",
        builder = webClientBuilder
    )

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
}
