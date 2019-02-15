package com.expedia.graphql.extensions

import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeUtil

/**
 * Useful public extension that renders a readable string from the given
 * graphql type no matter how deeply nested it is.
 * Eg: [[Int!]]!
 *
 * @return a string representation of the type taking list and non-null into account
 */
val GraphQLType.deepName: String
    get() = GraphQLTypeUtil.simplePrint(this)
