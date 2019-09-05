package com.expedia.graphql.generator.types

import com.expedia.graphql.exceptions.InvalidInputFieldTypeException
import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getGraphQLDescription
import com.expedia.graphql.generator.extensions.getName
import com.expedia.graphql.generator.extensions.isGraphQLID
import com.expedia.graphql.generator.extensions.isInterface
import com.expedia.graphql.generator.extensions.safeCast
import graphql.schema.GraphQLArgument
import kotlin.reflect.KParameter

internal class ArgumentBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    @Throws(InvalidInputFieldTypeException::class)
    internal fun argument(parameter: KParameter): GraphQLArgument {

        if (parameter.isInterface()) {
            throw InvalidInputFieldTypeException(parameter)
        }

        val graphQLType = graphQLTypeOf(parameter.type, inputType = true, annotatedAsID = parameter.isGraphQLID())

        // Deprecation of arguments is currently unsupported: https://github.com/facebook/graphql/issues/197
        val builder = GraphQLArgument.newArgument()
            .name(parameter.getName())
            .description(parameter.getGraphQLDescription())
            .type(graphQLType.safeCast())

        generator.directives(parameter).forEach {
            builder.withDirective(it)
        }

        return config.hooks.onRewireGraphQLType(builder.build()).safeCast()
    }
}
