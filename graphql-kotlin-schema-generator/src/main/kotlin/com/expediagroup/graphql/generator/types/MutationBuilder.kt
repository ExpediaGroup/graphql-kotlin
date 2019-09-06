package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.exceptions.InvalidMutationTypeException
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TypeBuilder
import com.expediagroup.graphql.generator.extensions.getValidFunctions
import com.expediagroup.graphql.generator.extensions.isNotPublic
import graphql.schema.GraphQLObjectType

internal class MutationBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    fun getMutationObject(mutations: List<TopLevelObject>): GraphQLObjectType? {

        if (mutations.isEmpty()) {
            return null
        }

        val mutationBuilder = GraphQLObjectType.Builder()
        mutationBuilder.name(config.topLevelNames.mutation)

        for (mutation in mutations) {
            if (mutation.kClass.isNotPublic()) {
                throw InvalidMutationTypeException(mutation.kClass)
            }

            generator.directives(mutation.kClass).forEach {
                mutationBuilder.withDirective(it)
            }

            mutation.kClass.getValidFunctions(config.hooks)
                .forEach {
                    val function = generator.function(it, config.topLevelNames.mutation, mutation.obj)
                    val functionFromHook = config.hooks.didGenerateMutationType(it, function)
                    mutationBuilder.field(functionFromHook)
                }
        }

        return mutationBuilder.build()
    }
}
