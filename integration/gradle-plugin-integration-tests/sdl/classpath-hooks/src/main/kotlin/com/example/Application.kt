package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import com.example.CustomFederatedHooks
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application
{
    @Bean
    fun federatedSchemaGeneratorHooks(): FederatedSchemaGeneratorHooks = CustomFederatedHooks()
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
