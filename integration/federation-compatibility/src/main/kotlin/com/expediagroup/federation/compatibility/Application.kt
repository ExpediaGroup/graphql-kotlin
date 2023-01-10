package com.expediagroup.federation.compatibility

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.server.WebFilter

@SpringBootApplication
class Application {

    @Bean
    fun corsFilter(): WebFilter = WebFilter { exchange, chain ->
        exchange.response.headers.add("Access-Control-Allow-Origin", "*")
        exchange.response.headers.add("Access-Control-Allow-Method", "GET, POST")
        exchange.response.headers.add("Access-Control-Allow-Headers", "content-type")
        chain.filter(exchange)
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
