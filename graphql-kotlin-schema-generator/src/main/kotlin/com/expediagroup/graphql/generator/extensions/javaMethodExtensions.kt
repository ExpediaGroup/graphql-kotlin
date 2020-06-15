/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.generator.extensions

import com.expediagroup.graphql.annotations.GraphQLIgnore
import java.lang.reflect.Method
import kotlin.reflect.KClass

/**
 * Used to check the java methods, like getters/setters for the [GraphQLIgnore] annotation.
 *
 * Some classes, like annotation arguments, loose the Kotlin annontation informatino on the contructor
 * and instead required checks on the generated java methods.
 */
internal fun Method.isGraphQLIgnored() = this.getAnnotation(GraphQLIgnore::class) != null

private fun <T : Annotation> Method.getAnnotation(annotationClass: KClass<T>) = this.annotations.find { it.annotationClass == annotationClass }
