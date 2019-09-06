package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TypeBuilder
import com.expediagroup.graphql.generator.extensions.getWrappedType
import graphql.schema.GraphQLList
import kotlin.reflect.KType

internal class ListBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun listType(type: KType, inputType: Boolean): GraphQLList {
        val wrappedType = graphQLTypeOf(type.getWrappedType(), inputType)
        return GraphQLList.list(wrappedType)
    }
}
