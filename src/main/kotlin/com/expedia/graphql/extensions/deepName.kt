package com.expedia.graphql.extensions

import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLType

/**
 * Useful public extension that renders a readable string from the given
 * graphql type no matter how deeply nested it is.
 * Eg: [[Int!]]!
 *
 * @return a string representation of the type taking list and non-null into account
 */
val GraphQLType.deepName: String
    get() = when {
        this is GraphQLNonNull -> "${wrappedType.deepName}!"
        this is GraphQLList -> "[${wrappedType.deepName}]"
        else -> name
    }
