package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelNames
import com.expediagroup.graphql.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.directives.KotlinSchemaDirectiveWiring
import com.expediagroup.graphql.execution.KotlinDataFetcherFactoryProvider
import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.SubTypeMapper
import com.expediagroup.graphql.generator.state.SchemaGeneratorState
import com.expediagroup.graphql.generator.state.TypesCache
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import graphql.schema.GraphQLCodeRegistry
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import java.lang.reflect.Field
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
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
    var state = spyk(SchemaGeneratorState(listOf("com.expediagroup.graphql")))
    var subTypeMapper = spyk(SubTypeMapper(listOf("com.expediagroup.graphql")))
    var cache = spyk(TypesCache(listOf("com.expediagroup.graphql")))
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
    private var inputPropertyBuilder: InputPropertyBuilder? = null
    private var unionBuilder: UnionBuilder? = null
    private var argumentBuilder: ArgumentBuilder? = null
    private var inputObjectBuilder: InputObjectBuilder? = null

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

        inputPropertyBuilder = spyk(InputPropertyBuilder(generator))
        every { generator.inputProperty(any(), any()) } answers {
            inputPropertyBuilder!!.inputProperty(it.invocation.args[0] as KProperty<*>, it.invocation.args[1] as KClass<*>)
        }

        scalarBuilder = spyk(ScalarBuilder(generator))
        every { generator.scalarType(any(), any()) } answers {
            scalarBuilder!!.scalarType(it.invocation.args[0] as KType, it.invocation.args[1] as Boolean)
        }

        objectBuilder = spyk(ObjectBuilder(generator))
        every { generator.objectType(any()) } answers {
            objectBuilder!!.objectType(it.invocation.args[0] as KClass<*>)
        }

        inputObjectBuilder = spyk(InputObjectBuilder(generator))
        every { generator.inputObjectType(any()) } answers {
            inputObjectBuilder!!.inputObjectType(it.invocation.args[0] as KClass<*>)
        }

        listBuilder = spyk(ListBuilder(generator))
        every { generator.listType(any(), any()) } answers {
            listBuilder!!.listType(it.invocation.args[0] as KType, it.invocation.args[1] as Boolean)
        }

        interfaceBuilder = spyk(InterfaceBuilder(generator))
        every { generator.interfaceType(any()) } answers {
            interfaceBuilder!!.interfaceType(it.invocation.args[0] as KClass<*>)
        }

        unionBuilder = spyk(UnionBuilder(generator))
        every { generator.unionType(any()) } answers {
            unionBuilder!!.unionType(it.invocation.args[0] as KClass<*>)
        }

        argumentBuilder = spyk(ArgumentBuilder(generator))
        every { generator.argument(any()) } answers {
            argumentBuilder!!.argument(it.invocation.args[0] as KParameter)
        }

        directiveBuilder = spyk(DirectiveBuilder(generator))
        every { generator.directives(any(), any()) } answers {
            val directives = directiveBuilder!!.directives(it.invocation.args[0] as KAnnotatedElement, it.invocation.args[1] as? KClass<*>)
            for (directive in directives) {
                state.directives[directive.name] = directive
            }
            directives
        }
        every { generator.fieldDirectives(any()) } answers {
            val directives = directiveBuilder!!.fieldDirectives(it.invocation.args[0] as Field)
            for (directive in directives) {
                state.directives[directive.name] = directive
            }
            directives
        }

        beforeTest()
    }

    open fun beforeTest() {}

    open fun beforeSetup() {}
}
