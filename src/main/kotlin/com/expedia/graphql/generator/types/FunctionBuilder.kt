package com.expedia.graphql.generator.types

import com.expedia.graphql.exceptions.InvalidInputFieldTypeException
import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getDeprecationReason
import com.expedia.graphql.generator.extensions.getGraphQLDescription
import com.expedia.graphql.generator.extensions.getKClass
import com.expedia.graphql.generator.extensions.getName
import com.expedia.graphql.generator.extensions.getTypeOfFirstArgument
import com.expedia.graphql.generator.extensions.isDataFetchingEnvironment
import com.expedia.graphql.generator.extensions.isGraphQLContext
import com.expedia.graphql.generator.extensions.isGraphQLIgnored
import com.expedia.graphql.generator.extensions.isInterface
import com.expedia.graphql.generator.extensions.safeCast
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLOutputType
import org.reactivestreams.Publisher
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.valueParameters

internal class FunctionBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    internal fun function(fn: KFunction<*>, target: Any? = null, abstract: Boolean = false): GraphQLFieldDefinition {
        val builder = GraphQLFieldDefinition.newFieldDefinition()
        builder.name(fn.name)
        builder.description(fn.getGraphQLDescription())

        fn.getDeprecationReason()?.let {
            builder.deprecate(it)
        }

        generator.directives(fn).forEach {
            builder.withDirective(it)
        }

        fn.valueParameters
            .filterNot { it.isGraphQLContext() }
            .filterNot { it.isGraphQLIgnored() }
            .filterNot { it.isDataFetchingEnvironment() }
            .forEach {
                // deprecation of arguments is currently unsupported: https://github.com/facebook/graphql/issues/197
                builder.argument(argument(it))
            }

        if (!abstract) {
            builder.dataFetcherFactory(config.dataFetcherFactoryProvider.functionDataFetcherFactory(target = target, kFunction = fn))
        }

        val typeFromHooks = config.hooks.willResolveMonad(fn.returnType)
        val returnType = getWrappedReturnType(typeFromHooks)
        builder.type(graphQLTypeOf(returnType).safeCast<GraphQLOutputType>())
        val graphQLType = builder.build()

        return config.hooks.onRewireGraphQLType(returnType, graphQLType).safeCast()
    }

    private fun getWrappedReturnType(returnType: KType): KType =
        when {
            returnType.getKClass().isSubclassOf(Publisher::class) -> returnType.getTypeOfFirstArgument()
            returnType.classifier == CompletableFuture::class -> returnType.getTypeOfFirstArgument()
            else -> returnType
        }

    @Throws(InvalidInputFieldTypeException::class)
    private fun argument(parameter: KParameter): GraphQLArgument {

        if (parameter.isInterface()) {
            throw InvalidInputFieldTypeException(parameter)
        }

        val builder = GraphQLArgument.newArgument()
            .name(parameter.getName())
            .description(parameter.getGraphQLDescription())
            .type(graphQLTypeOf(parameter.type, true).safeCast())

        generator.directives(parameter).forEach {
            builder.withDirective(it)
        }

        return config.hooks.onRewireGraphQLType(parameter.type, builder.build()).safeCast()
    }
}
