package com.expedia.graphql.sample.model

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
