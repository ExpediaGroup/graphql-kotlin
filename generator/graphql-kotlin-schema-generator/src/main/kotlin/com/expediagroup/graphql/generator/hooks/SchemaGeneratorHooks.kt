/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.generator.hooks

import com.expediagroup.graphql.generator.directives.DirectiveMetaInformation
import com.expediagroup.graphql.generator.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.generator.exceptions.EmptyInputObjectTypeException
import com.expediagroup.graphql.generator.exceptions.EmptyInterfaceTypeException
import com.expediagroup.graphql.generator.exceptions.EmptyMutationTypeException
import com.expediagroup.graphql.generator.exceptions.EmptyObjectTypeException
import com.expediagroup.graphql.generator.exceptions.EmptyQueryTypeException
import com.expediagroup.graphql.generator.exceptions.EmptySubscriptionTypeException
import com.expediagroup.graphql.generator.internal.extensions.isSubclassOf
import com.expediagroup.graphql.generator.internal.extensions.isValidAdditionalType
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLSchemaElement
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeUtil
import org.reactivestreams.Publisher
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
     * Called before generating a directive annotation information to the GraphQL directive.
     * This allows for special handling of the directive annotations.
     */
    fun willGenerateDirective(directiveInfo: DirectiveMetaInformation): GraphQLDirective? = null

    /**
     * Called after using reflection to generate the graphql object type but before returning it to the schema builder.
     * This allows for modifying the type info, like description or directives
     */
    fun willAddGraphQLTypeToSchema(type: KType, generatedType: GraphQLType): GraphQLType = generatedType

    /**
     * Called before resolving a return type to the GraphQL type.
     * This allows for changes in the supported return types or unwrapping of specific classes.
     */
    fun willResolveMonad(type: KType): KType = type

    /**
     * Called before resolving an input type to the input GraphQL type.
     * This allows for changes in the supported input values and unwrapping of custom types, like in an Optional.
     */
    fun willResolveInputMonad(type: KType): KType = type

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
    fun isValidProperty(kClass: KClass<*>, property: KProperty<*>): Boolean = true

    /**
     * Called when looking at the KClass functions to determine if it valid for adding to the generated schema.
     * If any filter returns false, it is rejected.
     */
    @Suppress("Detekt.FunctionOnlyReturningConstant")
    fun isValidFunction(kClass: KClass<*>, function: KFunction<*>): Boolean = true

    /**
     * Called when looking at the subscription functions to determine if it is using a valid return type.
     * By default, graphql-java supports org.reactivestreams.Publisher in the subscription execution strategy.
     * If you want to provide a custom execution strategy, you may need to override this hook.
     *
     * NOTE: You will most likely need to also override the [willResolveMonad] hook to allow for your custom type to be generated.
     */
    fun isValidSubscriptionReturnType(kClass: KClass<*>, function: KFunction<*>): Boolean = function.returnType.isSubclassOf(Publisher::class)

    /**
     * Allow for custom logic when adding additional types to filter out specific classes
     * or classes with other annotations or metadata.
     *
     * The default logic just filters out interfaces if inputType is true.
     */
    fun isValidAdditionalType(kClass: KClass<*>, inputType: Boolean): Boolean = kClass.isValidAdditionalType(inputType)

    /**
     * Called after `willGenerateGraphQLType` and before `didGenerateGraphQLType`.
     * Enables you to change the wiring, e.g. apply directives to alter the target type.
     */
    fun onRewireGraphQLType(generatedType: GraphQLSchemaElement, coordinates: FieldCoordinates? = null, codeRegistry: GraphQLCodeRegistry.Builder): GraphQLSchemaElement =
        wiringFactory.onWire(generatedType, coordinates, codeRegistry)

    /**
     * Called after wrapping the type based on nullity but before adding the generated type to the schema
     */
    @Suppress("Detekt.ThrowsCount")
    fun didGenerateGraphQLType(type: KType, generatedType: GraphQLType): GraphQLType {
        val unwrapped = GraphQLTypeUtil.unwrapNonNull(generatedType)
        return when {
            unwrapped is GraphQLInterfaceType && unwrapped.fieldDefinitions.isEmpty() -> throw EmptyInterfaceTypeException(ktype = type)
            unwrapped is GraphQLObjectType && unwrapped.fieldDefinitions.isEmpty() -> throw EmptyObjectTypeException(ktype = type)
            unwrapped is GraphQLInputObjectType && unwrapped.fieldDefinitions.isEmpty() -> throw EmptyInputObjectTypeException(ktype = type)
            else -> generatedType
        }
    }

    /**
     * Called after converting the function to a field definition but before adding to the query object to allow customization
     */
    fun didGenerateQueryField(kClass: KClass<*>, function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition): GraphQLFieldDefinition = fieldDefinition

    /**
     * Called after converting the function to a field definition but before adding to the mutation object to allow customization
     */
    fun didGenerateMutationField(kClass: KClass<*>, function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition): GraphQLFieldDefinition = fieldDefinition

    /**
     * Called after converting the function to a field definition but before adding to the subscription object to allow customization
     */
    fun didGenerateSubscriptionField(kClass: KClass<*>, function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition): GraphQLFieldDefinition = fieldDefinition

    /**
     * Called after generating the Query object but before adding it to the schema.
     */
    fun didGenerateQueryObject(type: GraphQLObjectType): GraphQLObjectType = if (type.fieldDefinitions.isEmpty()) {
        throw EmptyQueryTypeException
    } else {
        type
    }

    /**
     * Called after generating the Mutation object but before adding it to the schema.
     */
    fun didGenerateMutationObject(type: GraphQLObjectType): GraphQLObjectType = if (type.fieldDefinitions.isEmpty()) {
        throw EmptyMutationTypeException
    } else {
        type
    }

    /**
     * Called after generating the Subscription object but before adding it to the schema.
     */
    fun didGenerateSubscriptionObject(type: GraphQLObjectType): GraphQLObjectType = if (type.fieldDefinitions.isEmpty()) {
        throw EmptySubscriptionTypeException
    } else {
        type
    }

    val wiringFactory: KotlinDirectiveWiringFactory
        get() = KotlinDirectiveWiringFactory()
}
