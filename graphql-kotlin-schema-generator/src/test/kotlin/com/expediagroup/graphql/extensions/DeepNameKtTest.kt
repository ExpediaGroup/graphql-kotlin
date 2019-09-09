/*
 * Copyright 2019 Expedia Group, Inc.
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

package com.expediagroup.graphql.extensions

import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeVisitor
import graphql.util.TraversalControl
import graphql.util.TraverserContext
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class DeepNameKtTest {

    private class BasicType : GraphQLType {
        override fun getName() = "BasicType"

        override fun accept(context: TraverserContext<GraphQLType>, visitor: GraphQLTypeVisitor): TraversalControl =
            context.thisNode().accept(context, visitor)
    }

    private val basicType = BasicType()

    @Test
    fun `deepname of basic type`() {
        assertEquals(expected = "BasicType", actual = basicType.deepName)
    }

    @Test
    fun `deepname of list`() {
        val list = GraphQLList(basicType)
        assertEquals(expected = "[BasicType]", actual = list.deepName)
    }

    @Test
    fun `deepname of non null`() {
        val nonNull = GraphQLNonNull(basicType)
        assertEquals(expected = "BasicType!", actual = nonNull.deepName)
    }

    @Test
    fun `deepname of non null list of non nulls`() {
        val complicated = GraphQLNonNull(GraphQLList(GraphQLNonNull(basicType)))
        assertEquals(expected = "[BasicType!]!", actual = complicated.deepName)
    }
}
