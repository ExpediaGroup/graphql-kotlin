/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graalvm.spring

import com.expediagroup.graalvm.hooks.CustomHooks
import com.expediagroup.graalvm.schema.ArgumentQuery
import com.expediagroup.graalvm.schema.AsyncQuery
import com.expediagroup.graalvm.schema.BasicMutation
import com.expediagroup.graalvm.schema.ContextualQuery
import com.expediagroup.graalvm.schema.CustomScalarQuery
import com.expediagroup.graalvm.schema.EnumQuery
import com.expediagroup.graalvm.schema.ErrorQuery
import com.expediagroup.graalvm.schema.IdQuery
import com.expediagroup.graalvm.schema.InnerClassQuery
import com.expediagroup.graalvm.schema.ListQuery
import com.expediagroup.graalvm.schema.PolymorphicQuery
import com.expediagroup.graalvm.schema.ScalarQuery
import com.expediagroup.graalvm.schema.TypesQuery
import com.expediagroup.graalvm.schema.dataloader.ExampleDataLoader
import com.expediagroup.graalvm.schema.model.ExampleInterface
import com.expediagroup.graalvm.schema.model.ExampleUnion
import com.expediagroup.graalvm.schema.model.FirstImpl
import com.expediagroup.graalvm.schema.model.FirstUnionMember
import com.expediagroup.graalvm.schema.model.SecondImpl
import com.expediagroup.graalvm.schema.model.SecondUnionMember
import com.expediagroup.graphql.dataloader.KotlinDataLoaderRegistryFactory
import com.expediagroup.graphql.generator.GraphQLTypeResolver
import com.expediagroup.graphql.generator.SimpleTypeResolver
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class Application {

    @Bean
    fun hooks(): CustomHooks = CustomHooks()

    @Bean
    fun dataLoaderRegistryFactory(): KotlinDataLoaderRegistryFactory = KotlinDataLoaderRegistryFactory(
        ExampleDataLoader
    )

    @Bean
    fun typeResolver(): GraphQLTypeResolver = SimpleTypeResolver(
        mapOf(
            ExampleInterface::class to listOf(FirstImpl::class, SecondImpl::class),
            ExampleUnion::class to listOf(FirstUnionMember::class, SecondUnionMember::class)
        )
    )

    // queries
    @Bean
    fun argumentQuery() = ArgumentQuery()

    @Bean
    fun asyncQuery() = AsyncQuery()

    @Bean
    fun contextualQuery() = ContextualQuery()

    @Bean
    fun customScalarQuery() = CustomScalarQuery()

    @Bean
    fun enumQuery() = EnumQuery()

    @Bean
    fun errorQuery() = ErrorQuery()

    @Bean
    fun idQuery() = IdQuery()

    @Bean
    fun innerClassQuery() = InnerClassQuery()

    @Bean
    fun listQuery() = ListQuery()

    @Bean
    fun polymorphicQuery() = PolymorphicQuery()

    @Bean
    fun scalarQuery() = ScalarQuery()

    @Bean
    fun typesQuery() = TypesQuery()

    // mutations
    @Bean
    fun basicMutation() = BasicMutation()
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
