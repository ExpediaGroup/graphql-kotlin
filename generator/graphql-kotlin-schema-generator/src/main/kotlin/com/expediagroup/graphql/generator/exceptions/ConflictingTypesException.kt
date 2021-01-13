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

import kotlin.reflect.KClass

/**
 * Thrown when the schema being generated has two classes with the same GraphQLType name,
 * but they are not the same Kotlin class. We can not have the full package or classpath info
 * in the GraphQLType so all names must be unique.
 */
class ConflictingTypesException(kClass1: KClass<*>, kClass2: KClass<*>) :
    GraphQLKotlinException("Conflicting class names in schema generation [$kClass1, $kClass2]")
