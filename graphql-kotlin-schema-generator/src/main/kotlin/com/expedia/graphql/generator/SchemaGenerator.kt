package com.expedia.graphql.generator

import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.generator.state.SchemaGeneratorState
import com.expedia.graphql.generator.types.DirectiveBuilder
import com.expedia.graphql.generator.types.EnumBuilder
import com.expedia.graphql.generator.types.FunctionBuilder
import com.expedia.graphql.generator.types.InputObjectBuilder
import com.expedia.graphql.generator.types.InterfaceBuilder
import com.expedia.graphql.generator.types.ListBuilder
import com.expedia.graphql.generator.types.MutationBuilder
import com.expedia.graphql.generator.types.ObjectBuilder
import com.expedia.graphql.generator.types.PropertyBuilder
import com.expedia.graphql.generator.types.QueryBuilder
import com.expedia.graphql.generator.types.ScalarBuilder
import com.expedia.graphql.generator.types.SubscriptionBuilder
import com.expedia.graphql.generator.types.UnionBuilder
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLSchema
import java.lang.reflect.Field
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KType

internal class SchemaGenerator(internal val config: SchemaGeneratorConfig) {

    internal val state = SchemaGeneratorState(config.supportedPackages)
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
    private val listTypeBuilder = ListBuilder(this)
    private val functionTypeBuilder = FunctionBuilder(this)
    private val enumTypeBuilder = EnumBuilder(this)
    private val scalarTypeBuilder = ScalarBuilder(this)
    private val directiveTypeBuilder = DirectiveBuilder(this)

    internal fun generate(
        queries: List<TopLevelObject>,
        mutations: List<TopLevelObject>,
        subscriptions: List<TopLevelObject>
    ): GraphQLSchema {
        val builder = GraphQLSchema.newSchema()

        builder.query(queryBuilder.getQueryObject(queries))
        builder.mutation(mutationBuilder.getMutationObject(mutations))
        builder.subscription(subscriptionBuilder.getSubscriptionObject(subscriptions))

        // add interface/union implementations
        state.getValidAdditionalTypes().forEach {
            builder.additionalType(it)
        }

        builder.additionalDirectives(state.directives.values.toSet())
        builder.codeRegistry(codeRegistry.build())
        return config.hooks.willBuildSchema(builder).build()
    }

    internal fun function(fn: KFunction<*>, parentName: String, target: Any? = null, abstract: Boolean = false) =
        functionTypeBuilder.function(fn, parentName, target, abstract)

    internal fun property(prop: KProperty<*>, parentClass: KClass<*>) =
        propertyTypeBuilder.property(prop, parentClass)

    internal fun listType(type: KType, inputType: Boolean) =
        listTypeBuilder.listType(type, inputType)

    internal fun objectType(kClass: KClass<*>, interfaceType: GraphQLInterfaceType? = null) =
        objectTypeBuilder.objectType(kClass, interfaceType)

    internal fun inputObjectType(kClass: KClass<*>) =
        inputObjectTypeBuilder.inputObjectType(kClass)

    internal fun interfaceType(kClass: KClass<*>) =
        interfaceTypeBuilder.interfaceType(kClass)

    internal fun unionType(kClass: KClass<*>) =
        unionTypeBuilder.unionType(kClass)

    internal fun enumType(kClass: KClass<out Enum<*>>) =
        enumTypeBuilder.enumType(kClass)

    internal fun scalarType(type: KType, annotatedAsID: Boolean = false) =
        scalarTypeBuilder.scalarType(type, annotatedAsID)

    internal fun directives(element: KAnnotatedElement): List<GraphQLDirective> =
        directiveTypeBuilder.directives(element)

    internal fun fieldDirectives(field: Field): List<GraphQLDirective> =
        directiveTypeBuilder.fieldDirectives(field)
}
