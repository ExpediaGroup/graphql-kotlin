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

package com.expediagroup.graphql.examples.server.spring.query

import com.expediagroup.graphql.examples.server.spring.directives.LowercaseDirective
import com.expediagroup.graphql.examples.server.spring.directives.SpecificValueOnly
import com.expediagroup.graphql.examples.server.spring.directives.StringEval
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class CustomDirectiveQuery : Query {

    @GraphQLDescription("Returns a message modified by directives, lower case and non-empty")
    fun justWhisper(@StringEval(default = "default string", lowerCase = true) msg: String?): String? = msg

    @GraphQLDescription("This will only accept 'Cake' as input")
    @SpecificValueOnly("cake")
    fun onlyCake(msg: String): String = "<3"

    @GraphQLDescription("This will only accept 'IceCream' as input")
    @SpecificValueOnly("icecream")
    fun onlyIceCream(msg: String): String = "<3"

    @GraphQLDescription("Returns message modified by the manually wired directive to force lowercase")
    @LowercaseDirective
    fun forceLowercaseEcho(msg: String) = msg
}
