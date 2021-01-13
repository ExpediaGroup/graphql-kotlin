/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.generator.execution

import com.expediagroup.graphql.generator.SchemaGeneratorConfig
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.extensions.deepName
import com.expediagroup.graphql.generator.toSchema
import graphql.GraphQL
import graphql.schema.DataFetcher
import graphql.schema.DataFetcherFactories
import graphql.schema.DataFetcherFactory
import graphql.schema.DataFetchingEnvironment
import org.junit.jupiter.api.Test
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.test.assertEquals

class CustomDataFetcherTests {
    @Test
    fun `Custom DataFetcher can be used on functions`() {
        val config = SchemaGeneratorConfig(supportedPackages = listOf("com.expediagroup.graphql.generator"), dataFetcherFactoryProvider = CustomDataFetcherFactoryProvider())
        val schema = toSchema(queries = listOf(TopLevelObject(AnimalQuery())), config = config)

        val animalType = schema.getObjectType("Animal")
        assertEquals("AnimalDetails!", animalType.getFieldDefinition("details").type.deepName)

        val graphQL = GraphQL.newGraphQL(schema).build()
        val execute = graphQL.execute("{ findAnimal { id type details { specialId } } }")

        val data = execute.getData<Map<String, Any>>()["findAnimal"] as? Map<*, *>
        assertEquals(1, data?.get("id"))
        assertEquals("cat", data?.get("type"))

        val details = data?.get("details") as? Map<*, *>
        assertEquals(11, details?.get("specialId"))
    }
}

class AnimalQuery {
    fun findAnimal(): Animal = Animal(1, "cat")
}

@Suppress("DataClassShouldBeImmutable")
data class Animal(
    val id: Int,
    val type: String
) {
    lateinit var details: AnimalDetails
}

data class AnimalDetails(val specialId: Int)

class CustomDataFetcherFactoryProvider : SimpleKotlinDataFetcherFactoryProvider() {

    override fun propertyDataFetcherFactory(kClass: KClass<*>, kProperty: KProperty<*>): DataFetcherFactory<Any?> =
        if (kProperty.isLateinit) {
            DataFetcherFactories.useDataFetcher(AnimalDetailsDataFetcher())
        } else {
            super.propertyDataFetcherFactory(kClass, kProperty)
        }
}

class AnimalDetailsDataFetcher : DataFetcher<Any?> {

    override fun get(environment: DataFetchingEnvironment?): AnimalDetails {
        val animal = environment?.getSource<Animal>()
        val specialId = animal?.id?.plus(10) ?: 0
        return animal.let { AnimalDetails(specialId) }
    }
}
