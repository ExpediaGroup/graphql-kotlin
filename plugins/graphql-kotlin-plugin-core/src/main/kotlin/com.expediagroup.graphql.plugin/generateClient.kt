/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.plugin

import com.expediagroup.graphql.plugin.generator.GraphQLClientGenerator
import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorConfig
import com.squareup.kotlinpoet.FileSpec
import graphql.schema.idl.SchemaParser
import java.io.File

/**
 * Generate GraphQL client data classes from specified queries and target schema.
 */
fun generateClient(
    config: GraphQLClientGeneratorConfig,
    schema: File,
    queries: List<File>
): List<FileSpec> {
    val graphQLSchema = SchemaParser().parse(schema)
    val generator = GraphQLClientGenerator(graphQLSchema, config)
    return queries.map { queryFile ->
        generator.generate(queryFile)
    }
}
