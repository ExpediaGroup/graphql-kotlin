/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.plugin.maven

import com.expediagroup.graphql.plugin.generated.ExampleQuery
import com.expediagroup.graphql.plugin.generated.enums.CustomEnum
import com.expediagroup.graphql.plugin.generated.examplequery.BasicObject2
import com.expediagroup.graphql.plugin.generated.examplequery.SecondInterfaceImplementation
import com.expediagroup.graphql.plugin.generated.examplequery.ScalarWrapper
import com.expediagroup.graphql.plugin.generated.inputs.SimpleArgumentInput
import com.expediagroup.graphql.client.ktor.GraphQLKtorClient
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.io.File
import java.net.URL
import java.nio.file.Paths
import java.util.UUID

class GraphQLMavenPluginTest {

    @Test
    fun `verify introspection query run and schema file was downloaded`() {
        val buildDirectory = System.getProperty("buildDirectory")
        val schemaFile = File(buildDirectory, "mySchema.graphql")
        assertTrue(schemaFile.exists(), "schema file was downloaded")
    }

    @Test
    fun `verify client code was generated`() {
        val buildDirectory = System.getProperty("buildDirectory")
        val path = Paths.get(buildDirectory, "generated-sources", "graphql", "com", "expediagroup", "graphql", "plugin", "generated", "ExampleQuery.kt")
        assertTrue(path.toFile().exists(), "graphql client was generated")
    }

    @Test
    fun `verify client code was generated and can execute query`() {
        val graphQLEndpoint = System.getProperty("graphQLEndpoint")
        val client = GraphQLKtorClient(URL(graphQLEndpoint))

        val variables = ExampleQuery.Variables(simpleCriteria = SimpleArgumentInput(newName = "whatever", min = null, max = null))
        val query = ExampleQuery(variables)
        assertDoesNotThrow {
            runBlocking {
                val response = client.execute(query)
                assertTrue(response.errors == null)
                val data = response.data
                assertNotNull(data)
                val scalarResult = data?.scalarQuery
                assertTrue(scalarResult is ScalarWrapper)
                assertNotNull(scalarResult)
                assertTrue(scalarResult?.count is Int)
                assertTrue(scalarResult?.custom is UUID)
                assertEquals(CustomEnum.ONE, data?.enumQuery)
                val interfaceResult = data?.interfaceQuery
                assertTrue(interfaceResult is SecondInterfaceImplementation)
                val unionResult = data?.unionQuery
                assertTrue(unionResult is BasicObject2)
            }
        }
        client.close()
    }
}
