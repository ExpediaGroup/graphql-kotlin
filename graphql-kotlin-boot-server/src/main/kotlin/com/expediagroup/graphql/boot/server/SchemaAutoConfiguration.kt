/*
 * Copyright 2019 Expedia Group, Inc.
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

package com.expediagroup.graphql.boot.server

import com.expediagroup.graphql.SchemaGeneratorConfig
import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.boot.server.annotation.Mutation
import com.expediagroup.graphql.boot.server.annotation.Query
import com.expediagroup.graphql.boot.server.annotation.Subscription
import com.expediagroup.graphql.extensions.print
import com.expediagroup.graphql.toSchema
import graphql.schema.GraphQLSchema
import org.slf4j.LoggerFactory
import org.springframework.aop.framework.Advised
import org.springframework.aop.support.AopUtils
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.Optional

@ConditionalOnProperty(value = ["graphql.federation.enabled"], havingValue = "false", matchIfMissing = true)
@Configuration
class SchemaAutoConfiguration {

    private val logger = LoggerFactory.getLogger(SchemaAutoConfiguration::class.java)

    @Bean
    @ConditionalOnMissingBean
    fun schemaConfig(config: GraphQLConfigurationProperties): SchemaGeneratorConfig = SchemaGeneratorConfig(
            supportedPackages = config.packages
        )

    @Bean
    @ConditionalOnMissingBean
    fun schema(
        queries: Optional<List<Query>>,
        mutations: Optional<List<Mutation>>,
        subscriptions: Optional<List<Subscription>>,
        schemaConfig: SchemaGeneratorConfig
    ): GraphQLSchema {
        val schema = toSchema(
            config = schemaConfig,
            queries = queries.orElse(emptyList()).toTopLevelObjects(),
            mutations = mutations.orElse(emptyList()).toTopLevelObjects(),
            subscriptions = subscriptions.orElse(emptyList()).toTopLevelObjects()
        )

        logger.info(schema.print())
        return schema
    }
}

internal fun List<Any>.toTopLevelObjects() = this.map {
    val klazz = if (AopUtils.isAopProxy(it) && it is Advised) {
        it.targetSource.target::class
    } else {
        it::class
    }
    TopLevelObject(it, klazz)
}
