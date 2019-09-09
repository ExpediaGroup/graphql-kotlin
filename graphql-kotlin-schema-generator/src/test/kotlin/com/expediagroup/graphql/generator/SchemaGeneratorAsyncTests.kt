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

package com.expediagroup.graphql.generator

import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.getTestSchemaConfigWithHooks
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.testSchemaConfig
import com.expediagroup.graphql.toSchema
import graphql.schema.GraphQLNonNull
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KType
import kotlin.test.assertEquals

class SchemaGeneratorAsyncTests {

    private class MonadHooks : SchemaGeneratorHooks {
        override fun willResolveMonad(type: KType): KType = when (type.classifier) {
            Observable::class, Single::class, Maybe::class -> type.arguments.firstOrNull()?.type
            else -> type
        } ?: type
    }

    private val configWithRxJavaMonads = getTestSchemaConfigWithHooks(hooks = MonadHooks())

    @Test
    fun `SchemaGenerator strips type argument from CompletableFuture to support async servlet`() {
        val schema = toSchema(queries = listOf(TopLevelObject(AsyncQuery())), config = testSchemaConfig)
        val returnTypeName =
            (schema.getObjectType("Query").getFieldDefinition("asynchronouslyDo").type as? GraphQLNonNull)?.wrappedType?.name
        assertEquals("Int", returnTypeName)
    }

    @Test
    fun `SchemaGenerator strips type argument from RxJava2 Observable`() {
        val schema = toSchema(queries = listOf(TopLevelObject(RxJava2Query())), config = configWithRxJavaMonads)
        val returnTypeName =
            (schema.getObjectType("Query").getFieldDefinition("asynchronouslyDo").type as? GraphQLNonNull)?.wrappedType?.name
        assertEquals("Int", returnTypeName)
    }

    @Test
    fun `SchemaGenerator strips type argument from RxJava2 Single`() {
        val schema = toSchema(queries = listOf(TopLevelObject(RxJava2Query())), config = configWithRxJavaMonads)
        val returnTypeName =
            (schema.getObjectType("Query").getFieldDefinition("asynchronouslyDoSingle").type as? GraphQLNonNull)?.wrappedType?.name
        assertEquals("Int", returnTypeName)
    }

    @Test
    fun `SchemaGenerator strips type argument from RxJava2 Maybe`() {
        val schema = toSchema(queries = listOf(TopLevelObject(RxJava2Query())), config = configWithRxJavaMonads)
        val returnTypeName =
            (schema.getObjectType("Query").getFieldDefinition("maybe").type as? GraphQLNonNull)?.wrappedType?.name
        assertEquals("Int", returnTypeName)
    }

    class AsyncQuery {
        fun asynchronouslyDo(): CompletableFuture<Int> = CompletableFuture.completedFuture(1)
    }

    class RxJava2Query {
        fun asynchronouslyDo(): Observable<Int> = Observable.just(1)

        fun asynchronouslyDoSingle(): Single<Int> = Single.just(1)

        fun maybe(): Maybe<Int> = Maybe.empty()
    }
}
