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

import kotlin.reflect.KParameter

/**
 * GraphQL Interfaces and Unions cannot be used as arguments.
 * Specification reference: https://facebook.github.io/graphql/draft/#sec-Field-Arguments
 *
 * This specification is translated as following:
 * the type of a function argument cannot be a java / kotlin interface.
 *
 * Example of invalid use cases:
 *
 * class InvalidQuery {
 *      fun invalidInterfaceUsage (arg: AnInterface): String = arg.value
 *
 *      fun invalidUnionUsage (arg: UnionMarkup): Int = arg.property
 * }
 *
 * interface AnInterface {
 *    val value: String
 * }
 *
 * data class AnImplementation (
 *    override val value: String = "something"
 * ) : AnInterface
 *
 *
 * interface UnionMarkup
 *
 * data class PartOfUnion( val property: Int) : UnionMarkup
 */
class InvalidInputFieldTypeException(kParameter: KParameter) :
    GraphQLKotlinException("Argument cannot be an interface or a union, $kParameter")
