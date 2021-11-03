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

package com.expediagroup.graphql.generator.annotations

/**
 * Can be used on any field to set the return type.
 * The type name must match exactly to some additional type that was provided to the schema
 * generator, or it will fail on building the schema.
 *
 * Internally, the generator will check for this anntation first and just return a type reference with this name
 * instead of running reflection on the Kotlin code. This means that you can use this annotation with any Kotlin return type.
 * That does mean you could have runtime exceptions if the model you actually return doesn't match the GraphQL schema type.
 */
annotation class GraphQLType(val typeName: String)
