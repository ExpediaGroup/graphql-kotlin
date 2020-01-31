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

package com.expediagroup.graphql.generator

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.directives.DeprecatedDirective
import com.expediagroup.graphql.generator.state.ClassScanner
import com.expediagroup.graphql.generator.state.TypesCache
import com.expediagroup.graphql.generator.types.generateGraphQLType
import com.expediagroup.graphql.generator.types.generateMutations
import com.expediagroup.graphql.generator.types.generateQueries
import com.expediagroup.graphql.generator.types.generateSubscriptions
import graphql.Directives
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

/**
 * Generate a schema object given some configuration and top level objects for the queries, mutaitons, and subscriptions.
 *
 * This class maintains the state of the schema while generation is taking place. It is passed into the internal functions
 * so they can use the cache and add additional types and directives into the schema as they parse the Kotlin code.
 */
open class SchemaGenerator(internal val config: SchemaGeneratorConfig) {

    internal val classScanner = ClassScanner(config.supportedPackages)
    internal val cache = TypesCache(config.supportedPackages)
    internal val codeRegistry = GraphQLCodeRegistry.newCodeRegistry()
    internal val additionalTypes = mutableSetOf<GraphQLType>()
    internal val directives = ConcurrentHashMap<String, GraphQLDirective>()

    init {
        // NOTE: @include and @defer query directives are added by graphql-java by default
        // adding them explicitly here to keep it consistent with missing deprecated directive
        directives[Directives.IncludeDirective.name] = Directives.IncludeDirective
        directives[Directives.SkipDirective.name] = Directives.SkipDirective

        // graphql-kotlin default directives
        // @deprecated directive is a built-in directive that each GraphQL server should provide bu currently it is not added by graphql-java
        //   see https://github.com/graphql-java/graphql-java/issues/1598
        directives[DeprecatedDirective.name] = DeprecatedDirective
    }

    /**
     * Generate a schema given a list of objects to parse for the queries, mutations, and subscriptions.
     */
    open fun generateSchema(
        queries: List<TopLevelObject>,
        mutations: List<TopLevelObject> = emptyList(),
        subscriptions: List<TopLevelObject> = emptyList()
    ): GraphQLSchema {
        val builder = GraphQLSchema.newSchema()
        builder.query(generateQueries(this, queries))
        builder.mutation(generateMutations(this, mutations))
        builder.subscription(generateSubscriptions(this, subscriptions))

        // add unreferenced interface implementations
        additionalTypes.forEach {
            builder.additionalType(it)
        }

        config.hooks.onAddAdditionalTypes().forEach {
            builder.additionalType(generateGraphQLType(this, it, false, false))
        }

        builder.additionalDirectives(directives.values.toSet())
        builder.codeRegistry(codeRegistry.build())

        val schema = config.hooks.willBuildSchema(builder).build()

        classScanner.close()

        return schema
    }

    /**
     * Add all types with the following annotation to the schema.
     *
     * This is helpful for things like federation or combining external schemas
     */
    protected fun addAdditionalTypesWithAnnotation(annotation: KClass<*>) {
        classScanner.getClassesWithAnnotation(annotation)
            .map { generateGraphQLType(this, it.createType()) }
            .forEach {
                additionalTypes.add(it)
            }
    }
}
