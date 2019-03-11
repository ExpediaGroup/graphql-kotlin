package com.expedia.graphql.test.integration

import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.testSchemaConfig
import com.expedia.graphql.toSchema
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLTypeReference
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class NodeGraphTest {

    @Test
    fun nodeGraph() {
        val queries = listOf(TopLevelObject(NodeQuery()))

        val schema = toSchema(queries = queries, config = testSchemaConfig)

        assertEquals(expected = 1, actual = schema.queryType.fieldDefinitions.size)
        assertEquals(expected = "nodeGraph", actual = schema.queryType.fieldDefinitions.first().name)

        val nodeFields = (schema.typeMap["Node"] as? GraphQLObjectType)?.fieldDefinitions

        nodeFields?.forEach {
            assertFalse(it.type is GraphQLTypeReference, "Node.${it.name} is a GraphQLTypeReference")
        }
    }
}

/**
 * Represents a recursive type that references itself,
 * similar to a tree node structure.
 */
data class Node(
    val id: Int,
    val value: String,
    val parent: Node? = null,
    var children: List<Node> = emptyList()
)

class NodeQuery {

    private val root = Node(id = 0, value = "root")
    private val nodeA = Node(id = 1, value = "A", parent = root)
    private val nodeB = Node(id = 2, value = "B", parent = root)
    private val nodeC = Node(id = 3, value = "C", parent = nodeB)

    init {
        root.children = listOf(nodeA, nodeB)
        nodeB.children = listOf(nodeC)
    }

    fun nodeGraph() = root
}
