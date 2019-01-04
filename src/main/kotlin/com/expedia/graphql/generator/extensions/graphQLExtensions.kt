package com.expedia.graphql.generator.extensions

import com.expedia.graphql.exceptions.NestingNonNullTypeException
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLEnumValueDefinition
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInputObjectField
import graphql.schema.GraphQLInputObjectType
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLUnionType
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import kotlin.reflect.KType

/**
 * Map null and non-null types.
 * Throws an exception on wrapping a non-null graphql type twice.
 */
@Throws(NestingNonNullTypeException::class)
internal fun GraphQLType.wrapInNonNull(type: KType): GraphQLType = when {
    this is GraphQLNonNull -> throw NestingNonNullTypeException(this, type)
    type.isMarkedNullable -> this
    else -> GraphQLNonNull.nonNull(this)
}

internal fun GraphQLDirectiveContainer.getAllDirectives(): List<GraphQLDirective> {
    // A function without directives may still be rewired if the arguments have directives
    // see https://github.com/ExpediaDotCom/graphql-kotlin/wiki/Schema-Directives for details
    val mutableList = mutableListOf<GraphQLDirective>()

    mutableList.addAll(this.directives)

    if (this is GraphQLFieldDefinition) {
        this.arguments.forEach { mutableList.addAll(it.directives) }
    }

    return mutableList
}

@Suppress("UNCHECKED_CAST", "Detekt.ComplexMethod")
internal fun SchemaDirectiveWiring.wireOnEnvironment(environment: SchemaDirectiveWiringEnvironment<*>) =
    when (environment.element) {
        is GraphQLArgument -> onArgument(environment as SchemaDirectiveWiringEnvironment<GraphQLArgument>)
        is GraphQLEnumType -> onEnum(environment as SchemaDirectiveWiringEnvironment<GraphQLEnumType>)
        is GraphQLEnumValueDefinition -> onEnumValue(environment as SchemaDirectiveWiringEnvironment<GraphQLEnumValueDefinition>)
        is GraphQLFieldDefinition -> onField(environment as SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition>)
        is GraphQLInputObjectField -> onInputObjectField(environment as SchemaDirectiveWiringEnvironment<GraphQLInputObjectField>)
        is GraphQLInputObjectType -> onInputObjectType(environment as SchemaDirectiveWiringEnvironment<GraphQLInputObjectType>)
        is GraphQLInterfaceType -> onInterface(environment as SchemaDirectiveWiringEnvironment<GraphQLInterfaceType>)
        is GraphQLObjectType -> onObject(environment as SchemaDirectiveWiringEnvironment<GraphQLObjectType>)
        is GraphQLScalarType -> onScalar(environment as SchemaDirectiveWiringEnvironment<GraphQLScalarType>)
        is GraphQLUnionType -> onUnion(environment as SchemaDirectiveWiringEnvironment<GraphQLUnionType>)
        else -> environment.element
    }
