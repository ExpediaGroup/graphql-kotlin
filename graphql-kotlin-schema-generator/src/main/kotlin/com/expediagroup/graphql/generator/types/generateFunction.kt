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

internal fun generateFunction(generator: SchemaGenerator, fn: KFunction<*>, parentName: String, target: Any? = null, abstract: Boolean = false): GraphQLFieldDefinition {
    val builder = GraphQLFieldDefinition.newFieldDefinition()
    val functionName = fn.getFunctionName()
    builder.name(functionName)
    builder.description(fn.getGraphQLDescription())

    fn.getDeprecationReason()?.let {
        builder.deprecate(it)
        builder.withDirective(deprecatedDirectiveWithReason(it))
    }

    generateDirectives(generator, fn).forEach {
        builder.withDirective(it)
    }

    fn.getValidArguments().forEach {
        builder.argument(generateArgument(generator, it))
    }

    val typeFromHooks = generator.config.hooks.willResolveMonad(fn.returnType)
    val returnType = getWrappedReturnType(typeFromHooks)
    val graphQLOutputType = generateGraphQLType(generator, returnType).safeCast<GraphQLOutputType>()
    val graphQLType = builder.type(graphQLOutputType).build()
    val coordinates = FieldCoordinates.coordinates(parentName, functionName)

    if (!abstract) {
        val dataFetcherFactory = generator.config.dataFetcherFactoryProvider.functionDataFetcherFactory(target = target, kFunction = fn)
        generator.config.codeRegistry.dataFetcher(coordinates, dataFetcherFactory)
    }

    return generator.config.hooks.onRewireGraphQLType(graphQLType, coordinates, generator.config.codeRegistry).safeCast()
}
