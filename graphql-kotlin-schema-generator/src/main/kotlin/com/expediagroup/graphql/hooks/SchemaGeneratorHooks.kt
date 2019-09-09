/*
 * Copyright 2019 Expedia Group
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

package com.expediagroup.graphql.hooks

import com.expediagroup.graphql.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.execution.DataFetcherExecutionPredicate
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KType

/**
 * Collection of all the hooks when generating a schema.
 *
 * Hooks are lifecycle events that are called and triggered while the schema is building
 * that allow users to customize the schema.
 */
interface SchemaGeneratorHooks {
    /**
     * Called before the final GraphQL schema is built.
     * This doesn't prevent the called from rebuilding the final schema using java-graphql's functionality
     */
    fun willBuildSchema(builder: GraphQLSchema.Builder): GraphQLSchema.Builder = builder

    /**
     * Called before using reflection to generate the graphql object type for the given KType.
     * This allows supporting objects that the caller does not want to use reflection on for special handling
     */
    @Suppress("Detekt.FunctionOnlyReturningConstant")
    fun willGenerateGraphQLType(type: KType): GraphQLType? = null

    /**
     * Called after using reflection to generate the graphql object type but before returning it to the schema builder.
     * This allows for modifying the type info, like description or directives
     */
    fun willAddGraphQLTypeToSchema(type: KType, generatedType: GraphQLType): GraphQLType = generatedType

    /**
     * Called before resolving a KType to the GraphQL type.
     * This allows for a custom resolver on how to extract wrapped values, like in a CompletableFuture.
     */
    fun willResolveMonad(type: KType): KType = type

    /**
     * Called when looking at the KClass superclasses to determine if it valid for adding to the generated schema.
     * If any filter returns false, it is rejected.
     */
    @Suppress("Detekt.FunctionOnlyReturningConstant")
    fun isValidSuperclass(kClass: KClass<*>): Boolean = true

    /**
     * Called when looking at the KClass properties to determine if it valid for adding to the generated schema.
     * If any filter returns false, it is rejected.
     */
    @Suppress("Detekt.FunctionOnlyReturningConstant")
    fun isValidProperty(property: KProperty<*>): Boolean = true

    /**
     * Called when looking at the KClass functions to determine if it valid for adding to the generated schema.
     * If any filter returns false, it is rejected.
     */
    @Suppress("Detekt.FunctionOnlyReturningConstant")
    fun isValidFunction(function: KFunction<*>): Boolean = true

    /**
     * Called after `willGenerateGraphQLType` and before `didGenerateGraphQLType`.
     * Enables you to change the wiring, e.g. apply directives to alter the target type.
     */
    fun onRewireGraphQLType(generatedType: GraphQLType, coordinates: FieldCoordinates? = null, codeRegistry: GraphQLCodeRegistry.Builder? = null): GraphQLType =
        wiringFactory.onWire(generatedType, coordinates, codeRegistry)

    /**
     * Called after wrapping the type based on nullity but before adding the generated type to the schema
     */
    fun didGenerateGraphQLType(type: KType, generatedType: GraphQLType) = generatedType

    /**
     * Called after converting the function to a field definition but before adding to the schema to allow customization
     */
    fun didGenerateQueryType(function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition): GraphQLFieldDefinition = fieldDefinition

    /**
     * Called after converting the function to a field definition but before adding to the schema to allow customization
     */
    fun didGenerateMutationType(function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition): GraphQLFieldDefinition = fieldDefinition

    /**
     * Called after converting the function to a field definition but before adding to the schema to allow customization
     */
    fun didGenerateSubscriptionType(function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition): GraphQLFieldDefinition = fieldDefinition

    /**
     * Execute a predicate on each function parameters after their deserialization
     * If the execution is unsuccessful the `onFailure` method will be invoked
     */
    val dataFetcherExecutionPredicate: DataFetcherExecutionPredicate?
        get() = null

    val wiringFactory: KotlinDirectiveWiringFactory
        get() = KotlinDirectiveWiringFactory()
}
