package com.expedia.graphql.sample.query

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.sample.model.Animal
import com.expedia.graphql.sample.model.AnimalType
import com.expedia.graphql.sample.model.BodyPart
import com.expedia.graphql.sample.model.BodyType
import com.expedia.graphql.sample.model.Cat
import com.expedia.graphql.sample.model.Dog
import com.expedia.graphql.sample.model.Hand
import com.expedia.graphql.sample.model.Leg
import org.springframework.stereotype.Component

/**
 * Example query that displays the usage of interfaces and polymorphism.
 */
@Component
class PolymorphicQuery: Query {

    @GraphQLDescription("example interface usage - this query returns specific implementation of animal interface")
    fun animal(type: AnimalType): Animal? = when (type) {
        AnimalType.CAT -> Cat()
        AnimalType.DOG -> Dog()
        else -> null
    }

    @GraphQLDescription("example union usage - this query returns specific implementation of marker interface")
    fun bodyPart(type: BodyType): BodyPart? = when(type) {
        BodyType.HAND -> Hand()
        BodyType.LEG -> Leg()
        else -> null
    }
}