package com.expedia.graphql.exceptions

import kotlin.reflect.KClass

class InvalidSubscriptionTypeException(kClass: KClass<*>)
    : GraphQLKotlinException("Schema requires all subscriptions to be public, ${kClass.simpleName} has ${kClass.visibility} visibility modifier")
