/*
 * Copyright 2019 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.directives.deprecatedDirectiveWithReason
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TypeBuilder
import com.expediagroup.graphql.generator.extensions.getDeprecationReason
import com.expediagroup.graphql.generator.extensions.getFunctionName
import com.expediagroup.graphql.generator.extensions.getGraphQLDescription
import com.expediagroup.graphql.generator.extensions.getValidArguments
import com.expediagroup.graphql.generator.extensions.safeCast
import com.expediagroup.graphql.generator.types.utils.getWrappedReturnType
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLOutputType
import kotlin.reflect.KFunction

internal class FunctionBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    internal fun function(fn: KFunction<*>, parentName: String, target: Any?, abstract: Boolean): GraphQLFieldDefinition {
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
        val graphQLType = builder.type(graphQLOutputType).build()
        val coordinates = FieldCoordinates.coordinates(parentName, functionName)

        if (!abstract) {
            val dataFetcherFactory = config.dataFetcherFactoryProvider.functionDataFetcherFactory(target = target, kFunction = fn)
            generator.codeRegistry.dataFetcher(coordinates, dataFetcherFactory)
        }

        return config.hooks.onRewireGraphQLType(graphQLType, coordinates, codeRegistry).safeCast()
    }
}
