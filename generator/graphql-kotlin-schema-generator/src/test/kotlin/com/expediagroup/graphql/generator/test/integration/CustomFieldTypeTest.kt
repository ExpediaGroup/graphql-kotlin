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

package com.expediagroup.graphql.generator.test.integration

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.annotations.GraphQLType
import com.expediagroup.graphql.generator.defaultSupportedPackages
import com.expediagroup.graphql.generator.extensions.deepName
import com.expediagroup.graphql.generator.testSchemaConfig
import com.expediagroup.graphql.generator.toSchema
import graphql.Scalars
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import graphql.schema.GraphQLUnionType
import org.junit.jupiter.api.Test
import kotlin.reflect.full.createType
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertIsNot

class CustomFieldTypeTest {

    @Test
    fun `generate a custom nullable by default scalar type`() {
        val queries = listOf(TopLevelObject(CustomScalar()))
        val schema = toSchema(testSchemaConfig, queries)
        val returnType = schema.queryType.getField("scalarType").type
        assertIsNot<GraphQLNonNull>(returnType)
        assertEquals("String", returnType.deepName)
    }

    @Test
    fun `generate a custom non-null scalar type`() {
        val queries = listOf(TopLevelObject(CustomNonNullScalar()))
        val schema = toSchema(testSchemaConfig, queries)
        val returnType = schema.queryType.getField("nonNullScalarType").type
        assertIs<GraphQLNonNull>(returnType)
        assertEquals("String!", returnType.deepName)
    }

    @Test
    fun `generate a custom object type`() {
        val queries = listOf(TopLevelObject(CustomObject()))
        val fooCustom = GraphQLObjectType.newObject()
            .name("FooCustom")
            .field(
                GraphQLFieldDefinition.newFieldDefinition()
                    .name("value")
                    .type(Scalars.GraphQLBoolean)
            )
            .build()
        val config = SchemaGeneratorConfig(defaultSupportedPackages, additionalTypes = setOf(fooCustom))
        val schema = toSchema(config, queries)
        val returnType = schema.queryType.getField("customObject").type
        assertIs<GraphQLObjectType>(returnType)
        assertEquals("FooCustom", returnType.deepName)
    }

    @Test
    fun `generate a custom union type`() {
        val queries = listOf(TopLevelObject(CustomUnion()))
        val fooCustom = GraphQLUnionType.newUnionType()
            .name("FooOrBar")
            .possibleType(GraphQLTypeReference("Foo"))
            .possibleType(GraphQLTypeReference("Bar"))
            .typeResolver { env -> env.schema.getObjectType(env.fieldType.deepName) }
            .build()
        val config = SchemaGeneratorConfig(defaultSupportedPackages, additionalTypes = setOf(fooCustom))
        val generator = SchemaGenerator(config)
        val schema = generator.use {
            it.generateSchema(
                queries = queries,
                additionalTypes = setOf(
                    CustomUnion.Foo::class.createType(),
                    CustomUnion.Bar::class.createType(),
                )
            )
        }
        val returnType = schema.queryType.getField("customUnion").type
        assertIs<GraphQLUnionType>(returnType)
        assertEquals("FooOrBar", returnType.deepName)
    }

    class CustomScalar {
        @GraphQLType("String")
        fun scalarType(): Any? = null
    }

    class CustomNonNullScalar {
        @GraphQLType("String")
        fun nonNullScalarType(): Any = "hello"
    }

    class CustomObject {
        @GraphQLType("FooCustom")
        fun customObject(): Foo? = null

        class Foo(val value: Boolean)
    }

    class CustomUnion {
        @GraphQLType("FooOrBar")
        fun customUnion(): Any? = null

        class Foo(val value: Boolean)
        class Bar(val value: Boolean)
    }
}
