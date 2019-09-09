/*
 * Copyright 2019 Expedia Group
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

package com.expediagroup.graphql.exceptions

import graphql.schema.GraphQLType
import kotlin.reflect.KType

/**
 * Throws on nesting a non-null graphql type twice.
 */
class NestingNonNullTypeException(gType: GraphQLType, kType: KType)
    : GraphQLKotlinException("Already non null, don't need to nest, $gType, $kType")
