package com.expedia.graphql.federation.exception

import com.expedia.graphql.exceptions.GraphQLKotlinException

/**
 * Exception thrown if generated federated schema is invalid.
 */
class InvalidFederatedSchema(errors: List<String>) : GraphQLKotlinException(message = "Invalid federated schema:\n - ${errors.joinToString("\n - ")}")
