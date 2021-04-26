/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.generator.internal.types.utils

import com.expediagroup.graphql.generator.annotations.GraphQLValidObjectLocations
import com.expediagroup.graphql.generator.exceptions.InvalidObjectLocationException
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation

/**
 * Throws an exception if this KClass was used in an invalid location
 */
internal fun validateObjectLocation(kClass: KClass<*>, location: GraphQLValidObjectLocations.Locations) {
    kClass.findAnnotation<GraphQLValidObjectLocations>()?.let { annotation ->
        val validLocations = annotation.locations
        if (!validLocations.contains(location)) {
            throw InvalidObjectLocationException(kClass, validLocations)
        }
    }
}
