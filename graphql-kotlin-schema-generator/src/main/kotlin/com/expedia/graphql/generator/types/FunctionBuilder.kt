package com.expedia.graphql.generator.types

import com.expedia.graphql.directives.deprecatedDirectiveWithReason
import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getDeprecationReason
import com.expedia.graphql.generator.extensions.getFunctionName
import com.expedia.graphql.generator.extensions.getGraphQLDescription
import com.expedia.graphql.generator.extensions.getKClass
import com.expedia.graphql.generator.extensions.getTypeOfFirstArgument
import com.expedia.graphql.generator.extensions.getValidArguments
import com.expedia.graphql.generator.extensions.safeCast
import graphql.execution.DataFetcherResult
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLOutputType
import org.reactivestreams.Publisher
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf

internal class FunctionBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    internal fun function(fn: KFunction<*>, parentName: String, target: Any? = null, abstract: Boolean = false): GraphQLFieldDefinition {
        val builder = GraphQLFieldDefinition.newFieldDefinition()
        val functionName = fn.getFunctionName()
        builder.name(functionName)
        builder.description(fn.getGraphQLDescription())

        fn.getDeprecationReason()?.let {
            builder.deprecate(it)
            builder.withDirective(deprecatedDirectiveWithReason(it))
        }

        generator.directives(fn).forEach {
            builder.withDirective(it)
        }

        fn.getValidArguments().forEach {
            builder.argument(generator.argument(it))
        }

        val typeFromHooks = config.hooks.willResolveMonad(fn.returnType)
        val returnType = getWrappedReturnType(typeFromHooks)
        val graphQLOutputType = graphQLTypeOf(returnType).safeCast<GraphQLOutputType>()
        builder.type(graphQLOutputType)
        val graphQLType = builder.build()

        val coordinates = FieldCoordinates.coordinates(parentName, functionName)
        if (!abstract) {
            val dataFetcherFactory = config.dataFetcherFactoryProvider.functionDataFetcherFactory(target = target, kFunction = fn)
            generator.codeRegistry.dataFetcher(coordinates, dataFetcherFactory)
        }

        return config.hooks.onRewireGraphQLType(graphQLType, coordinates, codeRegistry).safeCast()
    }

    /**
     * These are the classes that can be returned from data fetchers (ie functions)
     * but we only want to expose the wrapped type in the schema.
     *
     * [Publisher] is used for subscriptions
     * [CompletableFuture] is used for asynchronous results
     * [DataFetcherResult] is used for returning data and errors in the same response
     */
    private fun getWrappedReturnType(returnType: KType): KType =
        when {
            returnType.getKClass().isSubclassOf(Publisher::class) -> returnType.getTypeOfFirstArgument()
            returnType.classifier == CompletableFuture::class -> returnType.getTypeOfFirstArgument()
            returnType.classifier == DataFetcherResult::class -> returnType.getTypeOfFirstArgument()
            else -> returnType
        }
}
