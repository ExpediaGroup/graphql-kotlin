package com.expedia.graphql.exceptions

import graphql.schema.GraphQLType

class CouldNotCastGraphQLType(type: GraphQLType) : GraphQLKotlinException("Could not cast GraphQLType $type")
