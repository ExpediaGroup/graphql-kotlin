package com.expediagroup.graphql.examples.server.ktor

import com.expediagroup.graphql.generator.annotations.*
import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.directives.*
import graphql.introspection.*
import graphql.schema.*

/**
 * This GraphQL directive allows to add constraints to the GraphQL Schema
 *
 * See https://opensource.expediagroup.com/graphql-kotlin/docs/schema-generator/customizing-schemas/directives/
 * See https://www.apollographql.com/blog/backend/validation/graphql-validation-using-directives/
 */
@GraphQLDirective(
    name = "constraint",
    description = "This GraphQL directive allows to add constraints to the GraphQL Schema - https://www.apollographql.com/blog/backend/validation/graphql-validation-using-directives/",
    locations = [
        Introspection.DirectiveLocation.FIELD_DEFINITION,
        Introspection.DirectiveLocation.INPUT_OBJECT,
        Introspection.DirectiveLocation.INPUT_FIELD_DEFINITION
    ]

)
annotation class ConstraintDirective(
    val format: String = "",
    val minLength: Int = 1,
    val maxLength: Int = Int.MAX_VALUE,
    val pattern: String = ""
)


class ConstraintDirectiveWiring: KotlinSchemaDirectiveWiring {

    override fun onField(environment: KotlinFieldDirectiveEnvironment): GraphQLFieldDefinition {
        val field: GraphQLFieldDefinition = environment.element
        val originalDataFetcher: DataFetcher<*> = environment.getDataFetcher()
        val newDataFetcher = DataFetcherFactories.wrapDataFetcher(originalDataFetcher) { _, value ->
            println("todo: handle the @constraint() directive")
            value.toString()
        }
        environment.setDataFetcher(newDataFetcher)
        return field
    }
}

data class DirectiveInput(
    @ConstraintDirective(format = "email")
    val email: String,
    @ConstraintDirective(minLength = 8, maxLength = 16)
    val deviceId: String
)

data class DirectivePayload(
    @ConstraintDirective(format = "email")
    val email: String,
    @ConstraintDirective(minLength = 8, maxLength = 16)
    val deviceId: String
)

