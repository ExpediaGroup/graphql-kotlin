package com.expedia.graphql.ext

import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLType

// Renders a readable string from the given graphql type no matter how deeply nested
// Eg: [[Int!]]!
val GraphQLType.deepName: String
    get() = when {
        this is GraphQLNonNull -> "${this.wrappedType.deepName}!"
        this is GraphQLList -> "[${this.wrappedType.deepName}]"
        else -> name
    }

// Creates a new builder by copying all the properties of the passed GraphQLFieldDefinition
fun GraphQLFieldDefinition.newBuilder(): GraphQLFieldDefinition.Builder {
    val builder = GraphQLFieldDefinition.Builder()
    builder.name(name)
    builder.argument(arguments)
    builder.type(type)
    builder.description(description)
    builder.dataFetcher(dataFetcher)
    builder.deprecate(deprecationReason)
    return builder
}
