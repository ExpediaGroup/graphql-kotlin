package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.directives.deprecatedDirectiveWithReason
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TypeBuilder
import com.expediagroup.graphql.generator.extensions.getDeprecationReason
import com.expediagroup.graphql.generator.extensions.getFunctionName
import com.expediagroup.graphql.generator.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.extensions.getKClass
import com.expediagroup.graphql.generator.extensions.getTypeOfFirstArgument
import com.expediagroup.graphql.generator.extensions.getValidArguments
import com.expediagroup.graphql.generator.extensions.safeCast
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
        builder.type(graphQLTypeOf(returnType).safeCast<GraphQLOutputType>())
        val graphQLType = builder.build()

        val coordinates = FieldCoordinates.coordinates(parentName, functionName)
        if (!abstract) {
            val dataFetcherFactory = config.dataFetcherFactoryProvider.functionDataFetcherFactory(target = target, kFunction = fn)
            generator.codeRegistry.dataFetcher(coordinates, dataFetcherFactory)
        }

        return config.hooks.onRewireGraphQLType(graphQLType, coordinates, codeRegistry).safeCast()
    }

    private fun getWrappedReturnType(returnType: KType): KType =
        when {
            returnType.getKClass().isSubclassOf(Publisher::class) -> returnType.getTypeOfFirstArgument()
            returnType.classifier == CompletableFuture::class -> returnType.getTypeOfFirstArgument()
            else -> returnType
        }
}
