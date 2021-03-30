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

package com.expediagroup.graphql.server.spring.subscriptions

/**
 * This is the best cast saftey we can get with the generics
 */
@Suppress("UNCHECKED_CAST")
internal fun castToMapOfStringString(payload: Any?): Map<String, String> {
    if (payload != null && payload is Map<*, *> && payload.isNotEmpty()) {
        if (payload.keys.first() is String && payload.values.first() is String) {
            return payload as Map<String, String>
        }
    }

    return emptyMap()
}
