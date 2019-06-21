package com.expedia.graphql.sample.extension

import com.expedia.graphql.directives.KotlinDirectiveWiringFactory
import com.expedia.graphql.execution.DataFetcherExecutionPredicate
import com.expedia.graphql.hooks.SchemaGeneratorHooks
import com.expedia.graphql.sample.validation.DataFetcherExecutionValidator
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import java.util.UUID
import javax.validation.Validator
import kotlin.reflect.KType

/**
 * Schema generator hook that adds additional scalar types.
 */
class CustomSchemaGeneratorHooks(validator: Validator, override val wiringFactory: KotlinDirectiveWiringFactory) : SchemaGeneratorHooks {

    /**
     * Register additional GraphQL scalar types.
     */
    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
        UUID::class -> graphqlUUIDType
        else -> null
    }

    override val dataFetcherExecutionPredicate: DataFetcherExecutionPredicate? = DataFetcherExecutionValidator(validator)
}

internal val graphqlUUIDType = GraphQLScalarType("UUID",
        "A type representing a formatted java.util.UUID",
        UUIDCoercing
)

private object UUIDCoercing : Coercing<UUID, String> {
    override fun parseValue(input: Any?): UUID = UUID.fromString(
            serialize(
                    input
            )
    )

    override fun parseLiteral(input: Any?): UUID? {
        val uuidString = (input as? StringValue)?.value
        return UUID.fromString(uuidString)
    }

    override fun serialize(dataFetcherResult: Any?): String = dataFetcherResult.toString()
}
