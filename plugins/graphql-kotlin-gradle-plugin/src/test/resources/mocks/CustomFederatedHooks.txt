package com.example

import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseLiteralException
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import java.util.UUID
import kotlin.reflect.KType

private val graphqlUUIDType = GraphQLScalarType.newScalar()
    .name("UUID")
    .description("Custom scalar representing UUID")
    .coercing(object : Coercing<UUID, String> {
        override fun parseValue(input: Any): UUID = UUID.fromString(
            serialize(input)
        )

        override fun parseLiteral(input: Any): UUID {
            val uuidString = (input as? StringValue)?.value
            return if (uuidString != null) {
                UUID.fromString(uuidString)
            } else {
                throw CoercingParseLiteralException("Unable to convert $input to Any scalar")
            }
        }

        override fun serialize(dataFetcherResult: Any): String = dataFetcherResult.toString()
    })
    .build()

class CustomFederatedHooks : FederatedSchemaGeneratorHooks(emptyList()) {

    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
        UUID::class -> graphqlUUIDType
        else -> super.willGenerateGraphQLType(type)
    }
}
