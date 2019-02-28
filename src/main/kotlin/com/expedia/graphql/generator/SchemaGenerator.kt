package com.expedia.graphql.generator

import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.generator.state.SchemaGeneratorState
import com.expedia.graphql.generator.types.DirectiveTypeBuilder
import com.expedia.graphql.generator.types.EnumTypeBuilder
import com.expedia.graphql.generator.types.FunctionTypeBuilder
import com.expedia.graphql.generator.types.InputObjectTypeBuilder
import com.expedia.graphql.generator.types.InterfaceTypeBuilder
import com.expedia.graphql.generator.types.ListTypeBuilder
import com.expedia.graphql.generator.types.MutationTypeBuilder
import com.expedia.graphql.generator.types.ObjectTypeBuilder
import com.expedia.graphql.generator.types.PropertyTypeBuilder
import com.expedia.graphql.generator.types.QueryTypeBuilder
import com.expedia.graphql.generator.types.ScalarTypeBuilder
import com.expedia.graphql.generator.types.UnionTypeBuilder
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

    private val queryBuilder = QueryTypeBuilder(this)
    private val mutationBuilder = MutationTypeBuilder(this)
    private val objectTypeBuilder = ObjectTypeBuilder(this)
    private val unionTypeBuilder = UnionTypeBuilder(this)
    private val interfaceTypeBuilder = InterfaceTypeBuilder(this)
    private val propertyTypeBuilder = PropertyTypeBuilder(this)
    private val inputObjectTypeBuilder = InputObjectTypeBuilder(this)
    private val listTypeBuilder = ListTypeBuilder(this)
    private val functionTypeBuilder = FunctionTypeBuilder(this)
    private val enumTypeBuilder = EnumTypeBuilder(this)
    private val scalarTypeBuilder = ScalarTypeBuilder(this)
    private val directiveTypeBuilder = DirectiveTypeBuilder(this)

    internal fun generate(queries: List<TopLevelObject>, mutations: List<TopLevelObject>): GraphQLSchema {
        val builder = GraphQLSchema.newSchema()

        builder.query(queryBuilder.getQueryObject(queries))

        builder.mutation(mutationBuilder.getMutationObject(mutations))

        state.getValidAdditionalTypes().forEach { builder.additionalType(it) }

        builder.additionalDirectives(state.directives)

        return config.hooks.willBuildSchema(builder).build()
    }

    internal fun function(fn: KFunction<*>, target: Any? = null, abstract: Boolean = false) =
        functionTypeBuilder.function(fn, target, abstract)

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

    internal fun directives(element: KAnnotatedElement): List<GraphQLDirective> {
        val directives = directiveTypeBuilder.directives(element)
        state.directives.addAll(directives)
        return directives
    }

    internal fun fieldDirectives(field: Field): List<GraphQLDirective> {
        val directives = directiveTypeBuilder.fieldDirectives(field)
        state.directives.addAll(directives)
        return directives
    }
}
