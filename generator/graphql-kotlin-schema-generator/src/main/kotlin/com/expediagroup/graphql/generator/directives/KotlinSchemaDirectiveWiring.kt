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

package com.expediagroup.graphql.generator.directives

import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLEnumValueDefinition
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLUnionType

/**
 * KotlinSchemaDirectiveWiring is used for enhancing/updating GraphQL type based on the specified directives.
 *
 *
 * NOTE: While the GraphQL spec allows specifying the directives on number of different types (@see graphql.introspection.Introspection#DirectiveLocation)
 * only fields have access to graphql.schema.DataFetcher that allows customizing runtime behavior.
 */
interface KotlinSchemaDirectiveWiring {

    /**
     * Modifies GraphQLObjectType by applying specified directive.
     *
     * @param environment the wiring element
     *
     * @return modified GraphQLObjectType
     */
    fun onObject(environment: KotlinSchemaDirectiveEnvironment<GraphQLObjectType>): GraphQLObjectType = environment.element

    /**
     * Modifies GraphQLFieldDefinition by applying specified directive.
     *
     * @param environment the wiring element
     *
     * @return modified GraphQLFieldDefinition
     */
    fun onField(environment: KotlinFieldDirectiveEnvironment): GraphQLFieldDefinition = environment.element

    /**
     * Modifies GraphQLArgument by applying specified directive.
     *
     * @param environment the wiring element
     *
     * @return modified GraphQLArgument
     */
    fun onArgument(environment: KotlinSchemaDirectiveEnvironment<GraphQLArgument>): GraphQLArgument = environment.element

    /**
     * Modifies GraphQLInterfaceType by applying specified directive.
     *
     * @param environment the wiring element
     *
     * @return modified GraphQLInterfaceType
     */
    fun onInterface(environment: KotlinSchemaDirectiveEnvironment<GraphQLInterfaceType>): GraphQLInterfaceType = environment.element

    /**
     * Modifies GraphQLUnionType by applying specified directive.
     *
     * @param environment the wiring element
     *
     * @return modified GraphQLUnionType
     */
    fun onUnion(environment: KotlinSchemaDirectiveEnvironment<GraphQLUnionType>): GraphQLUnionType = environment.element

    /**
     * Modifies GraphQLEnumType by applying specified directive.
     *
     * @param environment the wiring element
     *
     * @return modified GraphQLEnumType
     */
    fun onEnum(environment: KotlinSchemaDirectiveEnvironment<GraphQLEnumType>): GraphQLEnumType = environment.element

    /**
     * Modifies GraphQLEnumValueDefinition by applying specified directive.
     *
     * @param environment the wiring element
     *
     * @return modified GraphQLEnumValueDefinition
     */
    fun onEnumValue(environment: KotlinSchemaDirectiveEnvironment<GraphQLEnumValueDefinition>): GraphQLEnumValueDefinition = environment.element

    /**
     * Modifies GraphQLScalarType by applying specified directive.
     *
     * @param environment the wiring element
     *
     * @return modified GraphQLScalarType
     */
    fun onScalar(environment: KotlinSchemaDirectiveEnvironment<GraphQLScalarType>): GraphQLScalarType = environment.element

    /**
     * Modifies GraphQLInputObjectType by applying specified directive.
     *
     * @param environment the wiring element
     *
     * @return modified GraphQLInputObjectType
     */
    fun onInputObjectType(environment: KotlinSchemaDirectiveEnvironment<GraphQLInputObjectType>): GraphQLInputObjectType = environment.element

    /**
     * Modifies GraphQLInputObjectField by applying specified directive.
     *
     * @param environment the wiring element
     *
     * @return modified GraphQLInputObjectField
     */
    fun onInputObjectField(environment: KotlinSchemaDirectiveEnvironment<GraphQLInputObjectField>): GraphQLInputObjectField = environment.element

    @Suppress("UNCHECKED_CAST", "Detekt.ComplexMethod")
    fun wireOnEnvironment(environment: KotlinSchemaDirectiveEnvironment<*>) =
        when (environment.element) {
            is GraphQLArgument -> onArgument(environment as KotlinSchemaDirectiveEnvironment<GraphQLArgument>)
            is GraphQLEnumType -> onEnum(environment as KotlinSchemaDirectiveEnvironment<GraphQLEnumType>)
            is GraphQLEnumValueDefinition -> onEnumValue(environment as KotlinSchemaDirectiveEnvironment<GraphQLEnumValueDefinition>)
            is GraphQLFieldDefinition -> onField(environment as KotlinFieldDirectiveEnvironment)
            is GraphQLInputObjectField -> onInputObjectField(environment as KotlinSchemaDirectiveEnvironment<GraphQLInputObjectField>)
            is GraphQLInputObjectType -> onInputObjectType(environment as KotlinSchemaDirectiveEnvironment<GraphQLInputObjectType>)
            is GraphQLInterfaceType -> onInterface(environment as KotlinSchemaDirectiveEnvironment<GraphQLInterfaceType>)
            is GraphQLObjectType -> onObject(environment as KotlinSchemaDirectiveEnvironment<GraphQLObjectType>)
            is GraphQLScalarType -> onScalar(environment as KotlinSchemaDirectiveEnvironment<GraphQLScalarType>)
            is GraphQLUnionType -> onUnion(environment as KotlinSchemaDirectiveEnvironment<GraphQLUnionType>)
            else -> environment.element
        }
}
