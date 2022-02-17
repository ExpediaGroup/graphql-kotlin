/*
 * Copyright 2022 Expedia, Inc
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

import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeUtil

/**
 * Useful public extension that renders a readable string from the given
 * graphql type no matter how deeply nested it is.
 * Eg: [[Int!]]!
 *
 * @return a string representation of the type taking list and non-null into account
 */
val GraphQLType.deepName: String
    get() = GraphQLTypeUtil.simplePrint(this)

/**
 * Unwrap the type of all layers and return the last element.
 * This includes GraphQLNonNull and GraphQLList.
 * If the type is not wrapped, it will just be returned.
 */
fun GraphQLType.unwrapType(): GraphQLType = GraphQLTypeUtil.unwrapType(this).last()
