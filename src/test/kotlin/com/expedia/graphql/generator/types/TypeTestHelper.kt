package com.expedia.graphql.generator.types

import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.SubTypeMapper
import com.expedia.graphql.generator.state.SchemaGeneratorState
import com.expedia.graphql.generator.state.TypesCache
import com.expedia.graphql.hooks.NoopSchemaGeneratorHooks
import com.expedia.graphql.hooks.SchemaGeneratorHooks
import graphql.schema.GraphQLInterfaceType
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.test.BeforeTest

@Suppress(
    "Detekt.UnsafeCast",
    "Detekt.UnsafeCallOnNullableType",
    "Detekt.LongMethod"
)
internal open class TypeTestHelper {
    var generator = mockk<SchemaGenerator>()
    var config = mockk<SchemaGeneratorConfig>()
    var state = spyk(SchemaGeneratorState(listOf("com.expedia.graphql.generator.types")))
    var subTypeMapper = spyk(SubTypeMapper(listOf("com.expedia.graphql.generator.types")))
    var cache = spyk(TypesCache(listOf("com.expedia.graphql.generator.types")))
    var hooks: SchemaGeneratorHooks = NoopSchemaGeneratorHooks()

    private var scalarTypeBuilder: ScalarTypeBuilder? = null
    private var objectTypeBuilder: ObjectTypeBuilder? = null
    private var directiveTypeBuilder: DirectiveTypeBuilder? = null
    private var functionTypeBuilder: FunctionTypeBuilder? = null
    private var propertyTypeBuilder: PropertyTypeBuilder? = null

    @BeforeTest
    fun setup() {
        beforeSetup()

        every { generator.state } returns state
        every { state.cache } returns cache
        every { generator.config } returns config
        every { generator.subTypeMapper } returns subTypeMapper
        every { config.hooks } returns hooks
        every { config.dataFetcherFactory } returns null
        every { config.topLevelQueryName } returns "TestTopLevelQuery"
        every { config.topLevelMutationName } returns "TestTopLevelMutation"

        functionTypeBuilder = spyk(FunctionTypeBuilder(generator))
        every { generator.function(any(), any(), any()) } answers {
            functionTypeBuilder!!.function(it.invocation.args[0] as KFunction<*>, it.invocation.args[1], it.invocation.args[2] as Boolean)
        }

        propertyTypeBuilder = spyk(PropertyTypeBuilder(generator))
        every { generator.property(any(), any()) } answers {
            propertyTypeBuilder!!.property(it.invocation.args[0] as KProperty<*>, it.invocation.args[1] as KClass<*>)
        }

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
            val directives = directiveTypeBuilder!!.directives(it.invocation.args[0] as KAnnotatedElement)
            state.directives.addAll(directives)
            directives
        }

        beforeTest()
    }

    open fun beforeTest() {}

    open fun beforeSetup() {}
}
