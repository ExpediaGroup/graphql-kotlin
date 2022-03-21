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

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.exceptions.ConflictingFieldsException
import com.expediagroup.graphql.generator.exceptions.InvalidSubscriptionTypeException
import com.expediagroup.graphql.generator.internal.extensions.getValidFunctions
import com.expediagroup.graphql.generator.internal.extensions.isNotPublic
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLObjectType

internal fun generateSubscriptions(generator: SchemaGenerator, subscriptions: List<TopLevelObject>): GraphQLObjectType? {
    if (subscriptions.isEmpty()) {
        return null
    }

    val subscriptionBuilder = GraphQLObjectType.Builder()
    subscriptionBuilder.name(generator.config.topLevelNames.subscription)

    for (subscription in subscriptions) {
        val kClass = subscription.kClass

        if (kClass.isNotPublic()) {
            throw InvalidSubscriptionTypeException(kClass)
        }

        generateDirectives(generator, subscription.kClass, DirectiveLocation.OBJECT).forEach {
            subscriptionBuilder.withAppliedDirective(it)
        }

        kClass.getValidFunctions(generator.config.hooks)
            .forEach {
                if (generator.config.hooks.isValidSubscriptionReturnType(kClass, it).not()) {
                    throw InvalidSubscriptionTypeException(kClass, it)
                }

                val function = generateFunction(generator, it, generator.config.topLevelNames.subscription, subscription.obj)
                val functionFromHook = generator.config.hooks.didGenerateSubscriptionField(kClass, it, function)
                if (subscriptionBuilder.hasField(functionFromHook.name)) {
                    throw ConflictingFieldsException("Subscription(class: ${subscription.kClass})", it.name)
                }
                subscriptionBuilder.field(functionFromHook)
            }
    }

    return generator.config.hooks.didGenerateSubscriptionObject(subscriptionBuilder.build())
}
