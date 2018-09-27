package com.expedia.graphql.schema

import com.expedia.graphql.TopLevelObjectDef
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

    private val rxJavaMonadResolver: (KType) -> KType = { type ->
        when (type.classifier) {
            Observable::class, Single::class, Maybe::class -> type.arguments.firstOrNull()?.type
            else -> type
        } ?: type
    }
    private val testSchemaConfigWithRxJavaMonads =
        SchemaGeneratorConfig(supportedPackages = "com.expedia", monadResolver = rxJavaMonadResolver)

    @Test
    fun `SchemaGenerator strips type argument from CompletableFuture to support async servlet`() {
        val schema = toSchema(listOf(TopLevelObjectDef(AsyncQuery())), config = testSchemaConfig)
        val returnTypeName =
            (schema.getObjectType("TopLevelQuery").getFieldDefinition("asynchronouslyDo").type as GraphQLNonNull).wrappedType.name
        assertEquals("Int", returnTypeName)
    }

    @Test
    fun `SchemaGenerator strips type argument from RxJava2 Observable`() {
        val schema = toSchema(listOf(TopLevelObjectDef(RxJava2Query())), config = testSchemaConfigWithRxJavaMonads)
        val returnTypeName =
            (schema.getObjectType("TopLevelQuery").getFieldDefinition("asynchronouslyDo").type as GraphQLNonNull).wrappedType.name
        assertEquals("Int", returnTypeName)
    }

    @Test
    fun `SchemaGenerator strips type argument from RxJava2 Single`() {
        val schema = toSchema(listOf(TopLevelObjectDef(RxJava2Query())), config = testSchemaConfigWithRxJavaMonads)
        val returnTypeName =
            (schema.getObjectType("TopLevelQuery").getFieldDefinition("asynchronouslyDoSingle").type as GraphQLNonNull).wrappedType.name
        assertEquals("Int", returnTypeName)
    }

    @Test
    fun `SchemaGenerator strips type argument from RxJava2 Maybe`() {
        val schema = toSchema(listOf(TopLevelObjectDef(RxJava2Query())), config = testSchemaConfigWithRxJavaMonads)
        val returnTypeName =
            (schema.getObjectType("TopLevelQuery").getFieldDefinition("maybe").type as GraphQLNonNull).wrappedType.name
        assertEquals("Int", returnTypeName)
    }

    class AsyncQuery {
        fun asynchronouslyDo(): CompletableFuture<Int> {
            return CompletableFuture.completedFuture(1)
        }
    }

    class RxJava2Query {
        fun asynchronouslyDo(): Observable<Int> {
            return Observable.just(1)
        }

        fun asynchronouslyDoSingle(): Single<Int> {
            return Single.just(1)
        }

        fun maybe(): Maybe<Int> {
            return Maybe.empty()
        }
    }
}