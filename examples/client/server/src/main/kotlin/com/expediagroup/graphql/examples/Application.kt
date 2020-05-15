package com.expediagroup.graphql.examples

import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.util.UUID
import kotlin.reflect.KType

@SpringBootApplication
class Application {

    private val graphqlUUIDType = GraphQLScalarType.newScalar()
        .name("UUID")
        .description("Custom scalar representing UUID")
        .coercing(object : Coercing<UUID, String> {
            override fun parseValue(input: Any?): UUID = UUID.fromString(
                serialize(input)
            )

            override fun parseLiteral(input: Any?): UUID? {
                val uuidString = (input as? StringValue)?.value
                return UUID.fromString(uuidString)
            }

            override fun serialize(dataFetcherResult: Any?): String = dataFetcherResult.toString()
        })
        .build()

    @Bean
    fun customHooks(): SchemaGeneratorHooks = object : SchemaGeneratorHooks {
        override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
            UUID::class -> graphqlUUIDType
            else -> null
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
