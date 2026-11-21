/*
 * Copyright 2026 Expedia, Inc
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

@file:OptIn(ExperimentalSemanticIntrospection::class)

package com.expediagroup.graphql.server.spring

import com.expediagroup.graphql.generator.semantic.ExperimentalSemanticIntrospection
import com.expediagroup.graphql.generator.semantic.SemanticSearchResolver
import com.expediagroup.graphql.generator.semantic.withSemanticIntrospection
import graphql.schema.GraphQLSchema
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.env.Environment

/**
 * Adds semantic introspection's `Query._search` to the [GraphQLSchema] bean when the application provides a
 * [SemanticSearchResolver] bean and `graphql.introspection.enabled` isn't false.
 *
 * A post-processor rather than a `schema` bean parameter: the schema bean methods keep their signatures, and an
 * application's own [GraphQLSchema] bean gets it too. Register a resolver bean or call `withSemanticIntrospection`, not both.
 */
@Configuration(proxyBeanMethods = false)
@Import(SemanticIntrospectionPostProcessor::class)
class SemanticIntrospectionConfiguration

/**
 * Looks the resolver up only once the schema exists, so it isn't created before other post-processors.
 * More than one resolver bean fails startup.
 */
internal class SemanticIntrospectionPostProcessor(
    private val resolver: ObjectProvider<SemanticSearchResolver>,
    private val environment: Environment
) : BeanPostProcessor {
    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
        if (bean !is GraphQLSchema || !environment.getProperty("graphql.introspection.enabled", Boolean::class.java, true)) return bean
        return resolver.ifAvailable?.let(bean::withSemanticIntrospection) ?: bean
    }
}
