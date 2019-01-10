package com.expedia.graphql.integration

import com.expedia.graphql.TopLevelObject
import com.expedia.graphql.testSchemaConfig
import com.expedia.graphql.toSchema
import kotlin.test.Test
import kotlin.test.assertEquals

class NodeGraphTest {

    @Test
    fun nodeGraph() {
        val root = Node(id = 0, value = "root", parent = null, children = emptyList())
        val nodeA = Node(id = 1, value = "A", parent = root, children = emptyList())
        val nodeB = Node(id = 2, value = "B", parent = root, children = emptyList())
        val nodeC = Node(id = 3, value = "C", parent = nodeB, children = emptyList())

        root.children = listOf(nodeA, nodeB)
        nodeB.children = listOf(nodeC)

        val queries = listOf(TopLevelObject(NodeQuery()))

        val schema = toSchema(queries = queries, config = testSchemaConfig)

        assertEquals(expected = 1, actual = schema.queryType.fieldDefinitions.size)
        assertEquals(expected = "nodeGraph", actual = schema.queryType.fieldDefinitions.first().name)
    }
}

/**
 * Represents a recursive type that references itself,
 * similar to a tree node structure.
 */
data class Node(
    val id: Int,
    val value: String,
    val parent: Node?,
    var children: List<Node>
)

class NodeQuery {

    private val root = Node(id = 0, value = "root", parent = null, children = emptyList())
    private val nodeA = Node(id = 1, value = "A", parent = root, children = emptyList())
    private val nodeB = Node(id = 2, value = "B", parent = root, children = emptyList())
    private val nodeC = Node(id = 3, value = "C", parent = nodeB, children = emptyList())

    init {
        root.children = listOf(nodeA, nodeB)
        nodeB.children = listOf(nodeC)
    }

    fun nodeGraph() = root
}
