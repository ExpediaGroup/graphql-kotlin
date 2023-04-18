package com.example.hooks

import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import graphql.GraphQLContext
import graphql.execution.CoercedVariables
import graphql.language.StringValue
import graphql.language.Value
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.CoercingParseValueException
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import java.util.Locale
import java.util.UUID
import kotlin.reflect.KType

private val graphqlUUIDType = GraphQLScalarType.newScalar()
    .name("UUID")
    .description("Custom scalar representing UUID")
    .coercing(object : Coercing<UUID, String> {
        override fun parseValue(input: Any, graphQLContext: GraphQLContext, locale: Locale): UUID = try {
            UUID.fromString(serialize(input))
        } catch (e: Exception) {
            throw CoercingParseValueException("Unable to convert value $input to UUID")
        }

        override fun parseLiteral(input: Value<*>, variables: CoercedVariables, graphQLContext: GraphQLContext, locale: Locale): UUID {
            val uuidString = (input as? StringValue)?.value
            return if (uuidString != null) {
                UUID.fromString(uuidString)
            } else {
                throw CoercingParseLiteralException("Unable to convert literal $input to UUID")
            }
        }

        override fun serialize(dataFetcherResult: Any, graphQLContext: GraphQLContext, locale: Locale): String = dataFetcherResult.toString()
    })
    .build()

class CustomFederatedHooks : FederatedSchemaGeneratorHooks(emptyList()) {

    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
        UUID::class -> graphqlUUIDType
        else -> super.willGenerateGraphQLType(type)
    }
}
