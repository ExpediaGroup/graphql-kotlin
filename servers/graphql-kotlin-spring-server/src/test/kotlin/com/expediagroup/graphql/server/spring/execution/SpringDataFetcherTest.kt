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

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import graphql.schema.DataFetchingEnvironment
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SpringDataFetcherTest {

    private val context: ApplicationContext = mockk {
        every { getBean(MyService::class.java) } returns MyService()
        every { getBean(NotService::class.java) } throws NoSuchBeanDefinitionException(NotService::class.java)
    }
    private val dataFetchingEnvironment: DataFetchingEnvironment = mockk {
        every { containsArgument("service") } returns false
    }

    @Test
    fun `data is returned from spring annotated service argument`() {
        val dataFetcher = SpringDataFetcher(
            target = MyQuery(),
            fn = MyQuery::callService,
            applicationContext = context
        )

        val result = dataFetcher.get(dataFetchingEnvironment)

        assertEquals("foo", result)
    }

    @Test
    fun `data not returned if argument is missing @Autowired`() {
        val dataFetcher = SpringDataFetcher(
            target = MyQuery(),
            fn = MyQuery::callServiceNoAnnotation,
            applicationContext = context
        )

        assertFailsWith(IllegalArgumentException::class) {
            dataFetcher.get(dataFetchingEnvironment)
        }
    }

    @Test
    fun `data not returned if class is not a spring bean`() {
        val dataFetcher = SpringDataFetcher(
            target = MyQuery(),
            fn = MyQuery::callNotService,
            applicationContext = context
        )

        assertFailsWith(NoSuchBeanDefinitionException::class) {
            dataFetcher.get(dataFetchingEnvironment)
        }
    }

    @Test
    fun `data not returned if class is not a spring bean and argument is missing @Autowired`() {
        val dataFetcher = SpringDataFetcher(
            target = MyQuery(),
            fn = MyQuery::callNotServiceNoAnnotation,
            applicationContext = context
        )

        assertFailsWith(IllegalArgumentException::class) {
            dataFetcher.get(dataFetchingEnvironment)
        }
    }

    class MyQuery {
        fun callService(@Autowired @GraphQLIgnore service: MyService) = service.getData()
        fun callServiceNoAnnotation(service: MyService) = service.getData()
        fun callNotService(@Autowired @GraphQLIgnore service: NotService) = service.getData()
        fun callNotServiceNoAnnotation(service: NotService) = service.getData()
    }

    @Service
    class MyService {
        fun getData() = "foo"
    }

    class NotService {
        fun getData() = "foo"
    }
}
