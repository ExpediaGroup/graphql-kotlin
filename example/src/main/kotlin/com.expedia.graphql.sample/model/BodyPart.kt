package com.expedia.graphql.sample.model

interface BodyPart

enum class BodyType {
    HAND,
    LEG
}

class Hand: BodyPart {
    fun write(): String = "writing"
}

class Leg: BodyPart {
    fun walk(): String = "walking"
}