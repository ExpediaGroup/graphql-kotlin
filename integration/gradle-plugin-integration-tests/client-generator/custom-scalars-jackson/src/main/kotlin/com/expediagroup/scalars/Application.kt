package com.expediagroup.scalars

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    @Bean
    fun customHooks(): SchemaGeneratorHooks = CustomScalarHooks
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
