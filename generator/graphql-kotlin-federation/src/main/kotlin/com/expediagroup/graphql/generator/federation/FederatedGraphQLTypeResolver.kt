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

package com.expediagroup.graphql.generator.federation

import com.expediagroup.graphql.generator.ClasspathTypeResolver
import com.expediagroup.graphql.generator.GraphQLTypeResolver
import com.expediagroup.graphql.generator.SimpleTypeResolver
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.internal.state.ClassScanner
import kotlin.reflect.KClass

/**
 * Polymorphic GraphQL type resolver that can also locate federated entities.
 */
interface FederatedGraphQLTypeResolver : GraphQLTypeResolver {
    /**
     * Locate federated entities (i.e. types with `@key` directive)
     */
    fun locateEntities(): List<KClass<*>>
}

/**
 * Simple federated polymorphic type resolver that relies on provided polymorphic map and
 * list of entities.
 */
open class FederatedSimpleTypeResolver(
    polymorphicMap: Map<KClass<*>, List<KClass<*>>>,
    private val entities: List<KClass<*>>
) : SimpleTypeResolver(polymorphicMap), FederatedGraphQLTypeResolver {
    override fun locateEntities(): List<KClass<*>> = entities
}

/**
 * Classpath based federated polymorphic type resolver. Relies on ClassScanner to obtain
 * polymorphic info and to locate entities.
 */
open class FederatedClasspathTypeResolver(
    private val scanner: ClassScanner
) : ClasspathTypeResolver(scanner), FederatedGraphQLTypeResolver {
    override fun locateEntities(): List<KClass<*>> = scanner.getClassesWithAnnotation(KeyDirective::class)
}
