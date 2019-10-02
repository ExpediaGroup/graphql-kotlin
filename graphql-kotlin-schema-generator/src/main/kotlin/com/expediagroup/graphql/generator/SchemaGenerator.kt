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
import com.expediagroup.graphql.generator.state.SchemaGeneratorState
import com.expediagroup.graphql.generator.types.ArgumentBuilder
import com.expediagroup.graphql.generator.types.DirectiveBuilder
import com.expediagroup.graphql.generator.types.EnumBuilder
import com.expediagroup.graphql.generator.types.FunctionBuilder
import com.expediagroup.graphql.generator.types.InputObjectBuilder
import com.expediagroup.graphql.generator.types.InputPropertyBuilder
import com.expediagroup.graphql.generator.types.InterfaceBuilder
import com.expediagroup.graphql.generator.types.ListBuilder
import com.expediagroup.graphql.generator.types.MutationBuilder
import com.expediagroup.graphql.generator.types.ObjectBuilder
import com.expediagroup.graphql.generator.types.PropertyBuilder
import com.expediagroup.graphql.generator.types.QueryBuilder
import com.expediagroup.graphql.generator.types.ScalarBuilder
import com.expediagroup.graphql.generator.types.SubscriptionBuilder
import com.expediagroup.graphql.generator.types.UnionBuilder
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLSchema
import java.lang.reflect.Field
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType

@Suppress("LeakingThis")
open class SchemaGenerator(val config: SchemaGeneratorConfig) {

    val state = SchemaGeneratorState(config.supportedPackages)
    internal val subTypeMapper = SubTypeMapper(config.supportedPackages)
    internal val codeRegistry = GraphQLCodeRegistry.newCodeRegistry()

    private val queryBuilder = QueryBuilder(this)
    private val mutationBuilder = MutationBuilder(this)
    private val subscriptionBuilder = SubscriptionBuilder(this)
    private val objectTypeBuilder = ObjectBuilder(this)
    private val unionTypeBuilder = UnionBuilder(this)
    private val interfaceTypeBuilder = InterfaceBuilder(this)
    private val propertyTypeBuilder = PropertyBuilder(this)
    private val inputObjectTypeBuilder = InputObjectBuilder(this)
    private val inputPropertyBuilder = InputPropertyBuilder(this)
    private val listTypeBuilder = ListBuilder(this)
    private val functionTypeBuilder = FunctionBuilder(this)
    private val enumTypeBuilder = EnumBuilder(this)
    private val scalarTypeBuilder = ScalarBuilder(this)
    private val directiveTypeBuilder = DirectiveBuilder(this)
    private val argumentBuilder = ArgumentBuilder(this)

    open fun generate(
        queries: List<TopLevelObject>,
        mutations: List<TopLevelObject>,
        subscriptions: List<TopLevelObject>,
        builder: GraphQLSchema.Builder = GraphQLSchema.newSchema()
    ): GraphQLSchema {
        builder.query(queryBuilder.getQueryObject(queries))
        builder.mutation(mutationBuilder.getMutationObject(mutations))
        builder.subscription(subscriptionBuilder.getSubscriptionObject(subscriptions))

        // add unreferenced interface implementations
        state.additionalTypes.forEach {
            builder.additionalType(it)
        }

        builder.additionalDirectives(state.directives.values.toSet())
        builder.codeRegistry(codeRegistry.build())
        return config.hooks.willBuildSchema(builder).build()
    }

    open fun function(fn: KFunction<*>, parentName: String, target: Any? = null, abstract: Boolean = false) =
        functionTypeBuilder.function(fn, parentName, target, abstract)

    open fun property(prop: KProperty<*>, parentClass: KClass<*>) =
        propertyTypeBuilder.property(prop, parentClass)

    open fun listType(type: KType, inputType: Boolean) =
        listTypeBuilder.listType(type, inputType)

    open fun objectType(kClass: KClass<*>) =
        objectTypeBuilder.objectType(kClass)

    open fun inputObjectType(kClass: KClass<*>) =
        inputObjectTypeBuilder.inputObjectType(kClass)

    open fun inputProperty(prop: KProperty<*>, parentClass: KClass<*>) =
        inputPropertyBuilder.inputProperty(prop, parentClass)

    open fun interfaceType(kClass: KClass<*>) =
        interfaceTypeBuilder.interfaceType(kClass)

    open fun unionType(kClass: KClass<*>) =
        unionTypeBuilder.unionType(kClass)

    open fun enumType(kClass: KClass<out Enum<*>>) =
        enumTypeBuilder.enumType(kClass)

    open fun scalarType(type: KType, annotatedAsID: Boolean) =
        scalarTypeBuilder.scalarType(type, annotatedAsID)

    open fun directives(element: KAnnotatedElement, parentClass: KClass<*>? = null): List<GraphQLDirective> =
        directiveTypeBuilder.directives(element, parentClass)

    open fun fieldDirectives(field: Field): List<GraphQLDirective> =
        directiveTypeBuilder.fieldDirectives(field)

    open fun argument(parameter: KParameter): GraphQLArgument =
        argumentBuilder.argument(parameter)
}
