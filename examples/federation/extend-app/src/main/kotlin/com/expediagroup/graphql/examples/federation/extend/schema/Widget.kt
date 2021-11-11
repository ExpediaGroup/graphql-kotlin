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

package com.expediagroup.graphql.examples.federation.extend.schema

import com.expediagroup.graphql.generator.federation.directives.ExtendsDirective
import com.expediagroup.graphql.generator.federation.directives.ExternalDirective
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.federation.directives.RequiresDirective

/**
 * We do not own the "Widget" type in this service. We are extending it here with the new fields when we are given the @key "id"
 */
@KeyDirective(fields = FieldSet("id"))
@ExtendsDirective
class Widget(
    @ExternalDirective val id: Int,
    @ExternalDirective val listOfValues: List<Int>? = null,
    val randomValueFromExtend: Int
) {
    @RequiresDirective(FieldSet("listOfValues"))
    @Suppress("FunctionOnlyReturningConstant")
    fun extraStringFromExtend() = "This data is coming from extend-app! listOfValues=$listOfValues"
}
