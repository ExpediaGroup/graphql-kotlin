package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.schema.extensions.getArrayType
import com.expedia.graphql.schema.extensions.getTypeOfFirstArgument
import com.expedia.graphql.schema.generator.SchemaGenerator
import com.expedia.graphql.schema.generator.TypeBuilder
import graphql.schema.GraphQLList
import kotlin.reflect.KType

internal class ListTypeBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun arrayType(type: KType, inputType: Boolean): GraphQLList =
        GraphQLList.list(graphQLTypeOf(type.getArrayType(), inputType))

    internal fun listType(type: KType, inputType: Boolean): GraphQLList =
        GraphQLList.list(graphQLTypeOf(type.getTypeOfFirstArgument(), inputType))
}
