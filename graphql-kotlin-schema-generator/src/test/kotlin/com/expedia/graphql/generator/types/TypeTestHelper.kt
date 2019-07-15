package com.expedia.graphql.generator.types

import com.expedia.graphql.SchemaGeneratorConfig
import com.expedia.graphql.TopLevelNames
import com.expedia.graphql.directives.KotlinDirectiveWiringFactory
import com.expedia.graphql.directives.KotlinSchemaDirectiveWiring
import com.expedia.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.SubTypeMapper
import com.expedia.graphql.generator.state.SchemaGeneratorState
import com.expedia.graphql.generator.state.TypesCache
import com.expedia.graphql.hooks.SchemaGeneratorHooks
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLInterfaceType
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import java.lang.reflect.Field
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
    var state = spyk(SchemaGeneratorState(listOf("com.expedia.graphql")))
    var subTypeMapper = spyk(SubTypeMapper(listOf("com.expedia.graphql")))
    var cache = spyk(TypesCache(listOf("com.expedia.graphql")))
    val spyWiringFactory = spyk(KotlinDirectiveWiringFactory())
    var hooks: SchemaGeneratorHooks = object : SchemaGeneratorHooks {
        override val wiringFactory: KotlinDirectiveWiringFactory
            get() = spyWiringFactory
    }
    var dataFetcherFactory: KotlinDataFetcherFactoryProvider = KotlinDataFetcherFactoryProvider(hooks)

    private var scalarBuilder: ScalarBuilder? = null
    private var objectBuilder: ObjectBuilder? = null
    private var listBuilder: ListBuilder? = null
    private var interfaceBuilder: InterfaceBuilder? = null
    private var directiveBuilder: DirectiveBuilder? = null
    private var functionBuilder: FunctionBuilder? = null
    private var propertyBuilder: PropertyBuilder? = null

    @BeforeTest
    fun setup() {
        beforeSetup()

        every { generator.state } returns state
        every { state.cache } returns cache
        every { generator.config } returns config
        every { generator.subTypeMapper } returns subTypeMapper
        every { generator.codeRegistry } returns GraphQLCodeRegistry.newCodeRegistry()
        every { config.hooks } returns hooks
        every { config.dataFetcherFactoryProvider } returns dataFetcherFactory
        every { spyWiringFactory.getSchemaDirectiveWiring(any()) } returns object : KotlinSchemaDirectiveWiring {}

        every { config.topLevelNames } returns TopLevelNames(
            query = "TestTopLevelQuery",
            mutation = "TestTopLevelMutation",
            subscription = "TestTopLevelSubscription"
        )

        functionBuilder = spyk(FunctionBuilder(generator))
        every { generator.function(any(), any(), any(), any()) } answers {
            functionBuilder!!.function(it.invocation.args[0] as KFunction<*>, it.invocation.args[1] as String, it.invocation.args[2], it.invocation.args[3] as Boolean)
        }

        propertyBuilder = spyk(PropertyBuilder(generator))
        every { generator.property(any(), any()) } answers {
            propertyBuilder!!.property(it.invocation.args[0] as KProperty<*>, it.invocation.args[1] as KClass<*>)
        }

        scalarBuilder = spyk(ScalarBuilder(generator))
        every { generator.scalarType(any(), any()) } answers {
            scalarBuilder!!.scalarType(it.invocation.args[0] as KType, it.invocation.args[1] as Boolean)
        }

        objectBuilder = spyk(ObjectBuilder(generator))
        every { generator.objectType(any(), any()) } answers {
            objectBuilder!!.objectType(it.invocation.args[0] as KClass<*>, it.invocation.args[1] as GraphQLInterfaceType?)
        }

        listBuilder = spyk(ListBuilder(generator))
        every { generator.listType(any(), any()) } answers {
            listBuilder!!.listType(it.invocation.args[0] as KType, it.invocation.args[1] as Boolean)
        }

        interfaceBuilder = spyk(InterfaceBuilder(generator))
        every { generator.interfaceType(any()) } answers {
            interfaceBuilder!!.interfaceType(it.invocation.args[0] as KClass<*>)
        }

        directiveBuilder = spyk(DirectiveBuilder(generator))
        every { generator.directives(any()) } answers {
            val directives = directiveBuilder!!.directives(it.invocation.args[0] as KAnnotatedElement)
            state.directives.addAll(directives)
            directives
        }
        every { generator.fieldDirectives(any()) } answers {
            val directives = directiveBuilder!!.fieldDirectives(it.invocation.args[0] as Field)
            state.directives.addAll(directives)
            directives
        }

        beforeTest()
    }

    open fun beforeTest() {}

    open fun beforeSetup() {}
}
