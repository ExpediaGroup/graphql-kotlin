package com.expedia.graphql.federation

import com.expedia.graphql.exceptions.GraphQLKotlinException

/**
 * Exception thrown when we cannot resolve federated _entity query request.
 */
class FederationException(reason: String) : GraphQLKotlinException(message = reason)
