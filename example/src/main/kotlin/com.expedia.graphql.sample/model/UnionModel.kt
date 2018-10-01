package com.expedia.graphql.sample.model

interface AUnionType

data class LeftHand(val field: String): AUnionType

data class RightHand(val property: Int): AUnionType
