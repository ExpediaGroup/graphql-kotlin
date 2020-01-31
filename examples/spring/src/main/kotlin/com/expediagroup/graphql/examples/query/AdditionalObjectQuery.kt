/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.examples.query

import com.expediagroup.graphql.annotations.GraphQLContext
import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.examples.model.AdditionalObject
import com.expediagroup.graphql.spring.operations.Query
import org.springframework.stereotype.Component
import javax.management.Query

/**
 * Example usage of adding an additional object to the schema.
 */
@Component
class AdditionalObjectQuery : Query {

    @GraphQLDescription("query that uses a set as a list and relies on an object that isn't built elsewhere")
    fun additionalQuery(msg: String): Set<AdditionalObject> = setOf(AdditionalObject(msg))
}
