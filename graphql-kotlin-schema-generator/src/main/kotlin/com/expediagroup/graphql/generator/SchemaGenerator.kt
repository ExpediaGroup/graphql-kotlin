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
import com.expediagroup.graphql.directives.DeprecatedDirective
import com.expediagroup.graphql.generator.state.ClassScanner
import com.expediagroup.graphql.generator.state.TypesCache
import com.expediagroup.graphql.generator.types.generateGraphQLType
import graphql.Directives
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLType
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

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
     * Clean up the state and saved information after schema generation.
     *
     * Not required to call, as the state and subTypeMapper will be cleaned up by the garbage collector,
     * but it can help clean up memory early if it not being used.
     */
    fun close() {
        classScanner.close()
    }

    /**
     * Add all types with the following annotation to the schema.
     *
     * This is helpful for things like federation or combining external schemas
     */
    protected fun addAdditionalTypesWithAnnotation(annotation: KClass<*>) {
        classScanner.getClassesWithAnnotation(annotation)
            .map { generateGraphQLType(this, it.createType(), inputType = false, annotatedAsID = false) }
            .forEach {
                additionalTypes.add(it)
            }
    }
}
