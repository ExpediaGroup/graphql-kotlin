package com.expedia.graphql.federation.types

import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLUnionType

/**
 * Generates union of all types that use the  @key directive, including both types native to the schema and extended types.
 */
@Suppress("SpreadOperator")
fun generateEntityFieldDefinition(federatedTypes: Set<String>): GraphQLFieldDefinition {
    val possibleTypes = federatedTypes
        .map { GraphQLTypeReference(it) }
        .toTypedArray()

    return GraphQLFieldDefinition.newFieldDefinition()
        .name("_entities")
        .description("Union of all types that use the @key directive, including both types native to the schema and extended types")
        .argument(GraphQLArgument.newArgument()
            .name("representations")
            .type(GraphQLNonNull(
                GraphQLList(
                    GraphQLNonNull(
                        GraphQLTypeReference(ANY_SCALAR_TYPE.name)
                    )
                )
            ))
            .build())
        .type(GraphQLNonNull(
            GraphQLList(
                GraphQLUnionType.newUnionType()
                    .name("_Entity")
                    .possibleTypes(*possibleTypes)
                    .build()
            )
        ))
        .build()
}
