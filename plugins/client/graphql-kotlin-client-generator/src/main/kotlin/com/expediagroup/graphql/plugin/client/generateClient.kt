/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.plugin.client

import com.expediagroup.graphql.plugin.client.generator.GraphQLScalar
import com.expediagroup.graphql.plugin.client.generator.GraphQLSerializer
import com.expediagroup.graphql.plugin.client.generator.GraphQLClientGeneratorConfig
import com.expediagroup.graphql.plugin.client.generator.GraphQLClientGenerator
import com.squareup.kotlinpoet.FileSpec
import graphql.parser.ParserOptions
import java.io.File

/**
 * Generate GraphQL client data classes from specified queries and target schema.
 */
fun generateClient(
    packageName: String,
    allowDeprecated: Boolean = false,
    customScalarsMap: List<GraphQLScalar> = emptyList(),
    serializer: GraphQLSerializer = GraphQLSerializer.JACKSON,
    schemaPath: String,
    queries: List<File>,
    useOptionalInputWrapper: Boolean = false,
    parserOptions: ParserOptions.Builder.() -> Unit = {}
): List<FileSpec> {
    val customScalars = customScalarsMap.associateBy { it.scalar }
    val config = GraphQLClientGeneratorConfig(
        packageName = packageName,
        allowDeprecated = allowDeprecated,
        customScalarMap = customScalars,
        serializer = serializer,
        useOptionalInputWrapper = useOptionalInputWrapper,
        parserOptions = parserOptions
    )
    val generator = GraphQLClientGenerator(schemaPath, config)
    return generator.generate(queries)
}
