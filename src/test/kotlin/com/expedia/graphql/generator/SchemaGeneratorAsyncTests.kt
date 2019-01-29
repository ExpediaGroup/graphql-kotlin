package com.expedia.graphql.generator

import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.getTestSchemaConfigWithHooks
import com.expedia.graphql.hooks.SchemaGeneratorHooks
import com.expedia.graphql.testSchemaConfig
import com.expedia.graphql.toSchema
import graphql.schema.GraphQLNonNull
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import java.util.concurrent.CompletableFuture
import kotlin.reflect.KType
import kotlin.test.Test
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
        val schema = toSchema(listOf(TopLevelObject(AsyncQuery())), config = testSchemaConfig)
        val returnTypeName =
            (schema.getObjectType("Query").getFieldDefinition("asynchronouslyDo").type as? GraphQLNonNull)?.wrappedType?.name
        assertEquals("Int", returnTypeName)
    }

    @Test
    fun `SchemaGenerator strips type argument from RxJava2 Observable`() {
        val schema = toSchema(listOf(TopLevelObject(RxJava2Query())), config = configWithRxJavaMonads)
        val returnTypeName =
            (schema.getObjectType("Query").getFieldDefinition("asynchronouslyDo").type as? GraphQLNonNull)?.wrappedType?.name
        assertEquals("Int", returnTypeName)
    }

    @Test
    fun `SchemaGenerator strips type argument from RxJava2 Single`() {
        val schema = toSchema(listOf(TopLevelObject(RxJava2Query())), config = configWithRxJavaMonads)
        val returnTypeName =
            (schema.getObjectType("Query").getFieldDefinition("asynchronouslyDoSingle").type as? GraphQLNonNull)?.wrappedType?.name
        assertEquals("Int", returnTypeName)
    }

    @Test
    fun `SchemaGenerator strips type argument from RxJava2 Maybe`() {
        val schema = toSchema(listOf(TopLevelObject(RxJava2Query())), config = configWithRxJavaMonads)
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
