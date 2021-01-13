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

package com.expediagroup.graphql.generator.exceptions

import com.expediagroup.graphql.generator.internal.extensions.getSimpleName
import kotlin.reflect.KType

/**
 * Thrown when schema input object type does not expose any fields. Since GraphQL always requires you to explicitly specify all the fields down to scalar values, using an input object type without
 * any defined fields would result in a field that is impossible to query without producing an error.
 *
 * @see [Issue 568](https://github.com/graphql/graphql-spec/issues/568)
 */
class EmptyInputObjectTypeException(ktype: KType) : GraphQLKotlinException("Invalid ${ktype.getSimpleName(isInputType = true)} input object type - input object does not expose any fields.")
