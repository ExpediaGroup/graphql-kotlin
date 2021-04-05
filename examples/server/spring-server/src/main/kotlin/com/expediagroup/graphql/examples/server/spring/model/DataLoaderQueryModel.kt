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

package com.expediagroup.graphql.examples.server.spring.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore

data class Employee(
    val name: String,

    @GraphQLIgnore
    val companyId: Int,

    @GraphQLDescription("A list of unique skills")
    val skills: Set<String> = emptySet()
) {
    @GraphQLDescription("This value will be populated by a custom data fetcher using data loaders")
    lateinit var company: Company
}

data class Company(val id: Int, val name: String)
