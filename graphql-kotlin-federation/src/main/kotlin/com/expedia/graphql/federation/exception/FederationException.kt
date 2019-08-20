package com.expedia.graphql.federation.exception

import com.expedia.graphql.exceptions.GraphQLKotlinException

/**
 * Exception thrown when we cannot resolve federated _entity query request.
 */
class FederationException(reason: String) : GraphQLKotlinException(message = reason)
