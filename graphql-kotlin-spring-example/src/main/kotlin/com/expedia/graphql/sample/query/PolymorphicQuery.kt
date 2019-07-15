package com.expedia.graphql.sample.query

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.sample.model.Animal
import com.expedia.graphql.sample.model.AnimalType
import com.expedia.graphql.sample.model.BodyPart
import com.expedia.graphql.sample.model.Cat
import com.expedia.graphql.sample.model.Dog
import com.expedia.graphql.sample.model.LeftHand
import com.expedia.graphql.sample.model.RightHand
import org.springframework.stereotype.Component

/**
 * Example query that displays the usage of interfaces and polymorphism.
 */
@Component
class PolymorphicQuery: Query {

    @GraphQLDescription("this query returns specific animal type")
    fun animal(type: AnimalType): Animal? = when (type) {
        AnimalType.CAT -> Cat()
        AnimalType.DOG -> Dog()
        else -> null
    }

    fun dog(): Dog = Dog()

    @GraphQLDescription("this query can return either a RightHand or a LeftHand as part of the union of both type")
    fun whichHand(whichHand: String): BodyPart = when(whichHand) {
        "right" -> RightHand(12)
        else -> LeftHand("hello world")
    }
}
