package com.expedia.graphql.execution

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

interface DataFetcherFactoryConfig

data class DataFetcherFunctionConfig(val target: Any?, val kFunction: KFunction<*>) : DataFetcherFactoryConfig

data class DataFetcherPropertyConfig(val kProperty: KProperty<*>, val kClazz: KClass<*>) : DataFetcherFactoryConfig
