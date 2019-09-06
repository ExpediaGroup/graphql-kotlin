package com.expediagroup.graphql.generator.extensions

import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.valueParameters

internal fun KFunction<*>.getValidArguments(): List<KParameter> =
    this.valueParameters
        .filterNot { it.isGraphQLContext() }
        .filterNot { it.isGraphQLIgnored() }
        .filterNot { it.isDataFetchingEnvironment() }
