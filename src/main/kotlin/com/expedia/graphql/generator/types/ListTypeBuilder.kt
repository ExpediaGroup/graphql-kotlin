package com.expedia.graphql.generator.types

import com.expedia.graphql.generator.extensions.getArrayType
import com.expedia.graphql.generator.extensions.getTypeOfFirstArgument
import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import graphql.schema.GraphQLList
import kotlin.reflect.KType

internal class ListTypeBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun arrayType(type: KType, inputType: Boolean): GraphQLList =
        GraphQLList.list(graphQLTypeOf(type.getArrayType(), inputType))

    internal fun listType(type: KType, inputType: Boolean): GraphQLList =
        GraphQLList.list(graphQLTypeOf(type.getTypeOfFirstArgument(), inputType))
}
