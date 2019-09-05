package test.data.queries.simple

import kotlin.random.Random

class NestedQuery {
    fun getSimpleNestedObject(): List<SelfReferenceObject?> = listOf(SelfReferenceObject())
}

class SelfReferenceObject {
    val description: String? = "SelfReferenceObject"
    val id = Random.nextInt()
    fun nextObject(): List<SelfReferenceObject?> = listOf(SelfReferenceObject())
}
