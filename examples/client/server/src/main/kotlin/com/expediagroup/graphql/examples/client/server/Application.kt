/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.examples.client.server

import com.expediagroup.graphql.examples.client.server.scalars.graphqlULocaleType
import com.expediagroup.graphql.examples.client.server.scalars.graphqlUUIDType
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.ibm.icu.util.ULocale
import graphql.schema.GraphQLType
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.util.UUID
import kotlin.reflect.KType

@SpringBootApplication
class Application {

    @Bean
    fun customHooks(): SchemaGeneratorHooks = object : SchemaGeneratorHooks {
        override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
            UUID::class -> graphqlUUIDType
            ULocale::class -> graphqlULocaleType
            else -> null
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
