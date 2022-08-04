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

package com.expediagroup.graphql.examples.server.spring.model

import com.expediagroup.graphql.generator.annotations.GraphQLDeprecated
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore

@GraphQLDescription("A useful widget")
data class Widget(

    @GraphQLDescription("The widget's value that can be null")
    var value: Int? = null,

    @GraphQLDeprecated(message = "This field is deprecated", replaceWith = ReplaceWith("value"))
    @GraphQLDescription("The widget's deprecated value that shouldn't be used")
    val deprecatedValue: Int? = value,

    val listOfValues: List<Int>? = null,

    @GraphQLIgnore
    val ignoredField: String? = "ignored",

    private val hiddenField: String? = "hidden"
) {

    @GraphQLDescription("returns original value multiplied by target OR null if original value was null")
    fun multiplyValueBy(multiplier: Int) = value?.times(multiplier)
}
