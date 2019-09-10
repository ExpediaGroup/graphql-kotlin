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
