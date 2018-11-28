package com.expedia.graphql.schema.generator.types

import com.expedia.graphql.schema.SchemaGeneratorConfig
import com.expedia.graphql.schema.generator.SchemaGenerator
import com.expedia.graphql.schema.generator.SubTypeMapper
import com.expedia.graphql.schema.generator.TypesCache
import com.expedia.graphql.schema.generator.state.SchemaGeneratorState
import com.expedia.graphql.schema.hooks.NoopSchemaGeneratorHooks
import graphql.schema.GraphQLInterfaceType
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.test.BeforeTest

@Suppress(
    "Detekt.UnsafeCast",
    "Detekt.UnsafeCallOnNullableType"
)
internal open class TypeTestHelper {
    var generator = mockk<SchemaGenerator>()
    var config = mockk<SchemaGeneratorConfig>()
    var state = mockk<SchemaGeneratorState>()
    var subTypeMapper = mockk<SubTypeMapper>()
    var cache = mockk<TypesCache>()
    var hooks = NoopSchemaGeneratorHooks()
    var scalarTypeBuilder: ScalarTypeBuilder? = null
    var objectTypeBuilder: ObjectTypeBuilder? = null

    @BeforeTest
    fun setup() {
        every { generator.state } returns state
        every { state.cache } returns cache
        every { generator.config } returns config
        every { generator.subTypeMapper } returns subTypeMapper
        every { config.hooks } returns hooks
        every { config.dataFetcherFactory } returns null

        beforeTest()
    }

    fun mockScalarTypeBuilder() {
        scalarTypeBuilder = spyk(ScalarTypeBuilder(generator))
        every { generator.scalarType(any(), any()) } answers {
            scalarTypeBuilder!!.scalarType(it.invocation.args[0] as KType, it.invocation.args[1] as Boolean)
        }
    }

    fun mockObjectTypeBuilder() {
        objectTypeBuilder = spyk(ObjectTypeBuilder(generator))
        every { generator.objectType(any(), any()) } answers {
            objectTypeBuilder!!.objectType(it.invocation.args[0] as KClass<*>, it.invocation.args[1] as GraphQLInterfaceType?)
        }
    }

    fun mockTypeCache() {
        cache = spyk(TypesCache(listOf("com.expedia.graphql.schema.generator.types")))
        every { state.cache } returns cache
    }

    fun mockSubTypeMapper() {
        subTypeMapper = spyk(SubTypeMapper(listOf("com.expedia.graphql.schema.generator.types")))
        every { generator.subTypeMapper } returns subTypeMapper
    }

    open fun beforeTest() {}
}
