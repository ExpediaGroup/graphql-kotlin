package com.example

import com.example.hooks.CustomFederatedHooks
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class Application {

    @Bean
    open fun federatedSchemaGeneratorHooks(): FederatedSchemaGeneratorHooks = CustomFederatedHooks()
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
