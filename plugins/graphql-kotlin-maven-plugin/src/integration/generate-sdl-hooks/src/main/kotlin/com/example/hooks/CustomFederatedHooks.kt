package com.example.hooks

import com.expediagroup.graphql.federation.FederatedSchemaGeneratorHooks
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import java.util.UUID
import kotlin.reflect.KType

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

class CustomFederatedHooks : FederatedSchemaGeneratorHooks(emptyList()) {

    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
        UUID::class -> graphqlUUIDType
        else -> super.willGenerateGraphQLType(type)
    }
}
