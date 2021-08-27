/*
 * Copyright 2021 Expedia, Inc
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
 * Thrown when Object, Input Object, Interface, Union or Enum name contains non-alphanumeric ASCII or underscore characters.
 */
class InvalidGraphQLNameException(kClass: KClass<*>, name: String) :
    GraphQLKotlinException("The class $kClass specifies invalid GraphQL $name name, only alphanumeric ASCII characters with underscores are allowed")
