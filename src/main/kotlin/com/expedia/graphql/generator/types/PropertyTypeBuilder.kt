package com.expedia.graphql.generator.types

import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getPropertyDeprecationReason
import com.expedia.graphql.generator.extensions.getPropertyDescription
import com.expedia.graphql.generator.extensions.isPropertyGraphQLID
import graphql.schema.DataFetcherFactory
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLOutputType
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

@Suppress("Detekt.UnsafeCast")
internal class PropertyTypeBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {
    internal fun property(prop: KProperty<*>, parentClass: KClass<*>): GraphQLFieldDefinition {
        val propertyType = graphQLTypeOf(type = prop.returnType, annotatedAsID = prop.isPropertyGraphQLID(parentClass)) as GraphQLOutputType

        val fieldBuilder = GraphQLFieldDefinition.newFieldDefinition()
            .description(prop.getPropertyDescription(parentClass))
            .name(prop.name)
            .type(propertyType)
            .deprecate(prop.getPropertyDeprecationReason(parentClass))

        generator.directives(prop).forEach {
            fieldBuilder.withDirective(it)
        }

        val field = if (config.dataFetcherFactory != null && prop.isLateinit) {
            updatePropertyFieldBuilder(propertyType, fieldBuilder, config.dataFetcherFactory)
        } else {
            fieldBuilder
        }.build()

        return config.hooks.onRewireGraphQLType(prop.returnType, field) as GraphQLFieldDefinition
    }

    private fun updatePropertyFieldBuilder(propertyType: GraphQLOutputType, fieldBuilder: GraphQLFieldDefinition.Builder, dataFetcherFactory: DataFetcherFactory<*>?): GraphQLFieldDefinition.Builder {
        val updatedFieldBuilder = if (propertyType is GraphQLNonNull) {
            val graphQLOutputType = propertyType.wrappedType as? GraphQLOutputType
            if (graphQLOutputType != null) fieldBuilder.type(graphQLOutputType) else fieldBuilder
        } else {
            fieldBuilder
        }

        return updatedFieldBuilder.dataFetcherFactory(dataFetcherFactory)
    }
}
