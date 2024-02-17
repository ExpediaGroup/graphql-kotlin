/*
 * Copyright 2024 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.directives

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore

/**
 * Annotation representing authorization policy scalar type that is used by the `@policy directive.
 *
 * @param value required authorization policy
 * @see [com.expediagroup.graphql.generator.federation.types.POLICY_SCALAR_TYPE]
 */
@LinkedSpec(FEDERATION_SPEC)
annotation class Policy(val value: String)

// this is a workaround for JVM lack of support nested arrays as annotation values
@GraphQLIgnore
annotation class Policies(val value: Array<Policy>)
