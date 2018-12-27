package com.expedia.graphql.generator.types

import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.SubTypeMapper
import com.expedia.graphql.generator.state.SchemaGeneratorState
import com.expedia.graphql.generator.state.TypesCache
import com.expedia.graphql.hooks.NoopSchemaGeneratorHooks
import graphql.schema.GraphQLInterfaceType
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlin.reflect.KAnnotatedElement
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
    var state = spyk(SchemaGeneratorState(listOf("com.expedia.graphql.generator.types")))
    var subTypeMapper = spyk(SubTypeMapper(listOf("com.expedia.graphql.generator.types")))
    var cache = spyk(TypesCache(listOf("com.expedia.graphql.generator.types")))
    var hooks = NoopSchemaGeneratorHooks()
    var scalarTypeBuilder: ScalarTypeBuilder? = null
    var objectTypeBuilder: ObjectTypeBuilder? = null
    var directiveTypeBuilder: DirectiveTypeBuilder? = null

    @BeforeTest
    fun setup() {
        every { generator.state } returns state
        every { state.cache } returns cache
        every { generator.config } returns config
        every { generator.subTypeMapper } returns subTypeMapper
        every { config.hooks } returns hooks
        every { config.dataFetcherFactory } returns null

        scalarTypeBuilder = spyk(ScalarTypeBuilder(generator))
        every { generator.scalarType(any(), any()) } answers {
            scalarTypeBuilder!!.scalarType(it.invocation.args[0] as KType, it.invocation.args[1] as Boolean)
        }

        objectTypeBuilder = spyk(ObjectTypeBuilder(generator))
        every { generator.objectType(any(), any()) } answers {
            objectTypeBuilder!!.objectType(it.invocation.args[0] as KClass<*>, it.invocation.args[1] as GraphQLInterfaceType?)
        }

        directiveTypeBuilder = spyk(DirectiveTypeBuilder(generator))
        every { generator.directives(any()) } answers {
            directiveTypeBuilder!!.directives(it.invocation.args[0] as KAnnotatedElement)
        }

        beforeTest()
    }

    open fun beforeTest() {}
}
