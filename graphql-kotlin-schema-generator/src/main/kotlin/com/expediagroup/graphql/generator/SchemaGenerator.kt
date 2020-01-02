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
import com.expediagroup.graphql.generator.state.SchemaGeneratorState
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLType

open class SchemaGenerator(val config: SchemaGeneratorConfig) {

    internal val state = SchemaGeneratorState(config.supportedPackages)
    internal val subTypeMapper = ClassScanner(config.supportedPackages)
    internal val codeRegistry = GraphQLCodeRegistry.newCodeRegistry()

    /**
     * Add a GraphQL type to be included in the schema that may not be directly used by a data fetcher.
     * This can include interface implementations or federated types.
     */
    fun addAdditionalType(type: GraphQLType) = this.state.additionalTypes.add(type)

    /**
     * Clean up the state and saved information after schema generation.
     *
     * Not required to call, as the state and subTypeMapper will be cleaned up by the garbage collector,
     * but it can help clean up memory early if it not being used.
     */
    fun close() {
        subTypeMapper.close()
    }

    /**
     * Return the classes with a certain annoation.
     *
     * We are exposing it as protected so we don't have to have
     * another class scanner open and we can instead reuse the [ClassScanner].
     */
    protected fun getClassesWithAnnotation(annotationName: String) = subTypeMapper.getClassesWithAnnotation(annotationName)
}
