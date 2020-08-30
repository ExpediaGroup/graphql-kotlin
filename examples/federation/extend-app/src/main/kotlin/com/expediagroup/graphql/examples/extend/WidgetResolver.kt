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

package com.expediagroup.graphql.examples.extend

import com.expediagroup.graphql.federation.execution.FederatedTypeResolver
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class WidgetResolver(private val randomNumberService: RandomNumberService) : FederatedTypeResolver<Widget> {
    override val typeName: String = "Widget"

    override suspend fun resolve(environment: DataFetchingEnvironment, representations: List<Map<String, Any>>): List<Widget?> = representations.map {
        // Extract the 'id' from the other service
        val id = it["id"]?.toString()?.toIntOrNull() ?: throw InvalidWidgetIdException()

        // If we needed to construct a Widget which has data from other APIs,
        // this is the place where we could call them with the widget id
        val valueFromExtend = randomNumberService.getInt()
        Widget(id, valueFromExtend)
    }

    class InvalidWidgetIdException : RuntimeException()
}
