package com.expedia.graphql.generator.types

import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getPropertyDeprecationReason
import com.expedia.graphql.generator.extensions.getPropertyDescription
import com.expedia.graphql.generator.extensions.isPropertyGraphQLID
import com.expedia.graphql.generator.extensions.safeCast
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLOutputType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal class PropertyBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun property(prop: KProperty<*>, parentClass: KClass<*>): GraphQLFieldDefinition {
        val propertyType = graphQLTypeOf(type = prop.returnType, annotatedAsID = prop.isPropertyGraphQLID(parentClass))
            .safeCast<GraphQLOutputType>()

        val fieldBuilder = GraphQLFieldDefinition.newFieldDefinition()
            .description(prop.getPropertyDescription(parentClass))
            .name(prop.name)
            .type(propertyType)
            .dataFetcherFactory(config.dataFetcherFactoryProvider.propertyDataFetcherFactory(kClass = parentClass, kProperty = prop))
            .deprecate(prop.getPropertyDeprecationReason(parentClass))

        generator.directives(prop).forEach {
            fieldBuilder.withDirective(it)
        }

        val field = fieldBuilder.build()

        return config.hooks.onRewireGraphQLType(prop.returnType, field).safeCast()
    }
}
