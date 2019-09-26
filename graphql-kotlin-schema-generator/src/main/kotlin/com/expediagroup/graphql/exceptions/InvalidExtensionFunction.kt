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

package com.expediagroup.graphql.exceptions

import java.lang.reflect.Method

/**
 * Thrown when a function or property is annotated as an extension but is not a global (static) method
 */
open class InvalidExtensionFunction(function: Method) :
    GraphQLKotlinException("Function ${function.name} is not a global Kotlin extension function")
