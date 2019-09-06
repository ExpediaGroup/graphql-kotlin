package com.expediagroup.graphql.sample.query

import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class NestedQuery : Query {
    fun getSimpleNestedObject(): List<SelfReferenceObject?> = listOf(SelfReferenceObject())
}

class SelfReferenceObject {
    val description: String? = "SelfReferenceObject"
    val id = Random.nextInt()
    fun nextObject(): List<SelfReferenceObject?> = listOf(SelfReferenceObject())
}
