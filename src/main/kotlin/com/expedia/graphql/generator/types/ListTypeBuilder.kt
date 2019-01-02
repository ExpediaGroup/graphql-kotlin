package com.expedia.graphql.generator.types

import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getWrappedType
import graphql.schema.GraphQLList
import kotlin.reflect.KType

internal class ListTypeBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun listType(type: KType, inputType: Boolean): GraphQLList {
        val wrappedType = graphQLTypeOf(type.getWrappedType(), inputType)
        return GraphQLList.list(wrappedType)
    }
}
