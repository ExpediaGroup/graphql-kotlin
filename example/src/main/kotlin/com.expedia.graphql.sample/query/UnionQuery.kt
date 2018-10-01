package com.expedia.graphql.sample.query

import org.springframework.stereotype.Component

@Component
class QueryWithUnion: Query {
    fun whichHand(whichHand: String): AUnionType = when(whichHand) {
        "right" -> RightHand(12)
        else -> LeftHand("hello world")
    }
}

interface AUnionType

data class LeftHand(val field: String): AUnionType

data class RightHand(val property: Int): AUnionType
