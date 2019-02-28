package com.expedia.graphql.sample.model

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.sample.directives.SimpleDirective

@GraphQLDescription("animal interface type")
interface Animal {
    @GraphQLDescription("common field of animals")
    val type: AnimalType

    @GraphQLDescription("common function of animals")
    fun sound(): String
}

@GraphQLDescription("enum holding all supported animal types")
@SimpleDirective
enum class AnimalType {
    CAT,
    @SimpleDirective
    DOG
}

@GraphQLDescription("dog is one of the implementations of animal")
class Dog: Animal {

    override val type: AnimalType
        get() = AnimalType.DOG

    override fun sound() = "bark"

    @GraphQLDescription("this is specific to dogs")
    fun doSomethingUseful(): String = "something useful"
}

@GraphQLDescription("cat is another implementation of animal")
class Cat: Animal {
    override val type: AnimalType
        get() = AnimalType.CAT

    override fun sound() = "meow"

    @GraphQLDescription("this is specific to cats")
    fun ignoreEveryone(): String = "ignore everyone"
}
