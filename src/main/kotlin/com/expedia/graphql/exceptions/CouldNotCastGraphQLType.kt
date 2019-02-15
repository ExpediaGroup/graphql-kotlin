package com.expedia.graphql.exceptions

import graphql.schema.GraphQLType

/**
 * Thrown when the casting a GraphQLType to some parent type is invalid
 */
class CouldNotCastGraphQLType(type: GraphQLType) : GraphQLKotlinException("Could not cast GraphQLType $type")
