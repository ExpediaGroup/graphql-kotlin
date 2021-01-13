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

package com.expediagroup.graphql.generator

import kotlin.reflect.KClass

/**
 * Encapsulates an object to use as a target for schema generation and the
 * class to be used for reflection.
 *
 * @param obj The target object (or proxy to target object)
 * @param kClass Optional KClass of the target (or the proxied object)
 */
data class TopLevelObject(val obj: Any?, val kClass: KClass<*>) {
    constructor(obj: Any) : this(obj, obj::class)
    constructor(kClass: KClass<*>) : this(null, kClass)
}
