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

package com.expediagroup.graphql.generator.exceptions

import com.expediagroup.graphql.generator.internal.extensions.getSimpleName
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

class InvalidSubscriptionTypeException(kClass: KClass<*>, kFunction: KFunction<*>? = null) :
    GraphQLKotlinException(
        "Schema requires all subscriptions to be public and return a valid type from the hooks. " +
            "${kClass.simpleName} has ${kClass.visibility} visibility modifier. " +
            if (kFunction != null) "The function return type is ${kFunction.returnType.getSimpleName()}" else ""
    )
