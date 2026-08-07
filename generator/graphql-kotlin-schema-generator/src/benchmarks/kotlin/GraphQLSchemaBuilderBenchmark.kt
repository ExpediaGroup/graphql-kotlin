/*
 * Copyright 2026 Expedia, Inc
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

package com.expediagroup.graphql.generator

import graphql.GraphQL
import graphql.Scalars.GraphQLString
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import graphql.schema.StaticDataFetcher
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Fork
import org.openjdk.jmh.annotations.Measurement
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.OutputTimeUnit
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(3)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
open class GraphQLSchemaBuilderBenchmark {
    @Param("500", "1000")
    var typeCount: Int = 0

    private lateinit var schemaParts: SchemaParts

    @Setup
    fun setUp() {
        schemaParts = createSchemaParts(typeCount)

        val standard = buildWithStandardBuilder()
        val fast = buildWithFastBuilder()
        check(standard.typeMap.keys == fast.typeMap.keys)

        val query = "{ root { id name next { id } } }"
        val standardResult = GraphQL.newGraphQL(standard).build().execute(query)
        val fastResult = GraphQL.newGraphQL(fast).build().execute(query)
        check(standardResult.errors.isEmpty())
        check(fastResult.errors.isEmpty())
        check(standardResult.getData<Any>() == fastResult.getData<Any>())
    }

    @Benchmark
    fun standardBuilder(): GraphQLSchema = buildWithStandardBuilder()

    @Benchmark
    fun fastBuilder(): GraphQLSchema = buildWithFastBuilder()

    private fun buildWithStandardBuilder(): GraphQLSchema = GraphQLSchema.newSchema()
        .query(schemaParts.query)
        .additionalTypes(schemaParts.types)
        .codeRegistry(GraphQLCodeRegistry.newCodeRegistry(schemaParts.codeRegistry).build())
        .build()

    private fun buildWithFastBuilder(): GraphQLSchema = GraphQLSchema.FastBuilder(
        GraphQLCodeRegistry.newCodeRegistry(schemaParts.codeRegistry),
        schemaParts.query,
        null,
        null
    )
        .addTypes(schemaParts.types)
        .withValidation(true)
        .build()

    private fun createSchemaParts(numberOfTypes: Int): SchemaParts {
        val types = linkedSetOf<GraphQLNamedType>()
        val codeRegistry = GraphQLCodeRegistry.newCodeRegistry()
        var nextType: GraphQLObjectType? = null

        for (index in numberOfTypes - 1 downTo 0) {
            val typeName = "Node$index"
            val typeBuilder = GraphQLObjectType.newObject()
                .name(typeName)
                .field { field -> field.name("id").type(GraphQLString) }
                .field { field -> field.name("name").type(GraphQLString) }

            codeRegistry
                .dataFetcher(FieldCoordinates.coordinates(typeName, "id"), StaticDataFetcher("node-$index"))
                .dataFetcher(FieldCoordinates.coordinates(typeName, "name"), StaticDataFetcher(typeName))

            if (nextType != null) {
                typeBuilder.field { field -> field.name("next").type(nextType) }
                codeRegistry.dataFetcher(
                    FieldCoordinates.coordinates(typeName, "next"),
                    StaticDataFetcher(emptyMap<String, Any>())
                )
            }

            nextType = typeBuilder.build()
            types.add(nextType)
        }

        val query = GraphQLObjectType.newObject()
            .name("Query")
            .field { field -> field.name("root").type(checkNotNull(nextType)) }
            .build()
        codeRegistry.dataFetcher(
            FieldCoordinates.coordinates("Query", "root"),
            StaticDataFetcher(emptyMap<String, Any>())
        )

        return SchemaParts(query, types, codeRegistry.build())
    }

    private data class SchemaParts(
        val query: GraphQLObjectType,
        val types: Set<GraphQLNamedType>,
        val codeRegistry: GraphQLCodeRegistry
    )
}
