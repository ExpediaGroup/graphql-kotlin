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

/**
 * This exception is thrown when there is an invalid GraphQL type in the schema being generated.
 *
 * This can occur when a class is marked as an input or output type but does not meet the necessary conditions.
 * For example, a class marked as an input type that is not an input class, or a class marked as an output type
 * that is not an output class.
 *
 * @property message the detail message string of this throwable.
 * @constructor Constructs a new InvalidGraphQLTypeException with the specified detail message.
 */

class InvalidGraphQLTypeException(message: String) : GraphQLKotlinException(message)
