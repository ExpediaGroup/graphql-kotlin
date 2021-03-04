package com.expediagroup.graphql.client.serialization.types.serializers

import com.expediagroup.graphql.client.serialization.serializers.GraphQLErrorPathSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GraphQLErrorPathSerializerTest {

    private val expectedData = ErrorPathWrapper(
        path = listOf("root", 1, "leaf")
    )

    private val expectedSerialized =
        """{
        |  "path": [
        |    "root",
        |    1,
        |    "leaf"
        |  ]
        |}
    """.trimMargin()

    @Test
    fun `verify serialization`() {
        val json = Json {
            prettyPrint = true
            // defaults to 4 spaces
            prettyPrintIndent = "  "
        }
        val serialized = json.encodeToString(expectedData)
        assertEquals(expectedSerialized, serialized)
    }

    @Test
    fun `verify deserialization`() {
        val json = Json
        val result = json.decodeFromString<ErrorPathWrapper>(expectedSerialized)
        assertEquals(expectedData, result)
    }

    @Serializable
    data class ErrorPathWrapper(
        val path: List<@Serializable(with = GraphQLErrorPathSerializer::class) Any>? = null
    )
}
