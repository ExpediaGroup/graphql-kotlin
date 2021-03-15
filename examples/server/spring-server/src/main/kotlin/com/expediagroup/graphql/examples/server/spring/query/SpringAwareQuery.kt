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

import com.expediagroup.graphql.examples.server.spring.model.Widget
import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.server.operations.Query
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

/**
 * Example query that showcases usage of `SpringDataFetcher`.
 */
@Component
class SpringAwareQuery : Query {

    @GraphQLDescription("retrieves Widget from the repository by ID")
    fun widgetById(
        @GraphQLIgnore @Autowired repository: WidgetRepository,
        @GraphQLDescription("The special ingredient") id: Int
    ): Widget? = repository.findWidget(id)

    @GraphQLDescription("retrieves all widgets from repository")
    fun availableWidgets(@GraphQLIgnore @Autowired repository: WidgetRepository): List<Widget> = repository.retrieveAllWidgets()
}

@Service
class WidgetRepository {

    private val widgets = mapOf(
        1 to Widget(value = 1),
        2 to Widget(value = 2)
    )

    fun findWidget(id: Int): Widget? = widgets[id]

    fun retrieveAllWidgets(): List<Widget> = widgets.values.toList()
}
