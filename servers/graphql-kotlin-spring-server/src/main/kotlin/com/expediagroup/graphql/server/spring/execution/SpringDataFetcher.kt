/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.server.spring.execution

import com.expediagroup.graphql.generator.execution.FunctionDataFetcher
import graphql.schema.DataFetchingEnvironment
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.ApplicationContext
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.javaType

/**
 * Spring aware function data fetcher that automatically autowires Spring beans as function parameters.
 */
open class SpringDataFetcher(
    target: Any?,
    fn: KFunction<*>,
    private val applicationContext: ApplicationContext
) : FunctionDataFetcher(target, fn) {

    override fun mapParameterToValue(param: KParameter, environment: DataFetchingEnvironment): Pair<KParameter, Any?>? =
        if (param.hasAnnotation<Autowired>()) {
            val qualifier = param.findAnnotation<Qualifier>()?.value
            if (qualifier != null) {
                param to applicationContext.getBean(qualifier)
            } else {
                param to applicationContext.getBean(param.type.javaType as Class<*>)
            }
        } else {
            super.mapParameterToValue(param, environment)
        }
}
