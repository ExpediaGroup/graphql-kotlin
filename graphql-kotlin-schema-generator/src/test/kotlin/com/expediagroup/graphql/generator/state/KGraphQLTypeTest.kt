/*
 * Copyright 2019 Expedia Group
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

package com.expediagroup.graphql.generator.state

import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeVisitor
import graphql.util.TraversalControl
import graphql.util.TraverserContext
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class KGraphQLTypeTest {

    @Suppress("Detekt.UnusedPrivateClass")
    private data class MyType(val id: Int = 0)
    private val graphQLType = object : GraphQLType {
        override fun getName(): String = "MyType"

        override fun accept(context: TraverserContext<GraphQLType>, visitor: GraphQLTypeVisitor): TraversalControl = context.thisNode().accept(context, visitor)
    }

    @Test
    fun `properties are set`() {
        val kGraphQLType = KGraphQLType(MyType::class, graphQLType)
        assertEquals(expected = MyType::class, actual = kGraphQLType.kClass)
        assertEquals(expected = graphQLType, actual = kGraphQLType.graphQLType)
    }
}
