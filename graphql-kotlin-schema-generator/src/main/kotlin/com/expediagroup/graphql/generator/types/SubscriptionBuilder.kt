package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.exceptions.InvalidSubscriptionTypeException
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TypeBuilder
import com.expediagroup.graphql.generator.extensions.getValidFunctions
import com.expediagroup.graphql.generator.extensions.isNotPublic
import graphql.schema.GraphQLObjectType

internal class SubscriptionBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    internal fun getSubscriptionObject(subscriptions: List<TopLevelObject>): GraphQLObjectType? {

        if (subscriptions.isEmpty()) {
            return null
        }

        val subscriptionBuilder = GraphQLObjectType.Builder()
        subscriptionBuilder.name(config.topLevelNames.subscription)

        for (subscription in subscriptions) {
            if (subscription.kClass.isNotPublic()) {
                throw InvalidSubscriptionTypeException(subscription.kClass)
            }

            subscription.kClass.getValidFunctions(config.hooks)
                .forEach {
                    val function = generator.function(it, config.topLevelNames.subscription, subscription.obj)
                    val functionFromHook = config.hooks.didGenerateSubscriptionType(it, function)
                    subscriptionBuilder.field(functionFromHook)
                }
        }

        return subscriptionBuilder.build()
    }
}
