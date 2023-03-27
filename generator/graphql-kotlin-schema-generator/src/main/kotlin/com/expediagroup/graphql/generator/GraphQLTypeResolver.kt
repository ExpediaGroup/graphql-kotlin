/*
 * Copyright 2023 Expedia, Inc
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

import com.expediagroup.graphql.generator.internal.state.ClassScanner
import java.io.Closeable
import kotlin.reflect.KClass

/**
 * Polymorphic types resolver.
 */
interface GraphQLTypeResolver : Closeable {
    /**
     * Given a class find all its subtypes.
     */
    fun getSubTypesOf(kclass: KClass<*>): List<KClass<*>>
}

/**
 * Map based polymorphic types resolver.
 */
open class SimpleTypeResolver(
    private val polymorphicMap: Map<KClass<*>, List<KClass<*>>>
) : GraphQLTypeResolver {

    override fun getSubTypesOf(kclass: KClass<*>): List<KClass<*>> = polymorphicMap[kclass] ?: emptyList()

    override fun close() {
        // no-op
    }
}

/**
 * Classpath based polymorphic type resolvers. Uses ClassScanner to find polymorphic info.
 */
open class ClasspathTypeResolver(
    private val scanner: ClassScanner
) : GraphQLTypeResolver {
    override fun getSubTypesOf(kclass: KClass<*>): List<KClass<*>> = scanner.getSubTypesOf(kclass)

    override fun close() {
        scanner.close()
    }
}
