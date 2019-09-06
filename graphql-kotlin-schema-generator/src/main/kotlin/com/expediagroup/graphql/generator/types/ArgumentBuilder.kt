package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.exceptions.InvalidInputFieldTypeException
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TypeBuilder
import com.expediagroup.graphql.generator.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.extensions.getName
import com.expediagroup.graphql.generator.extensions.isGraphQLID
import com.expediagroup.graphql.generator.extensions.isInterface
import com.expediagroup.graphql.generator.extensions.safeCast
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
