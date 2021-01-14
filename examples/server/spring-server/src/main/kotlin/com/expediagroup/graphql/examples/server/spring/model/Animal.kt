/*
 * Copyright 2021 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.examples.server.spring.model

import com.expediagroup.graphql.generator.annotations.GraphQLDescription

@GraphQLDescription("animal interface type")
interface Animal {
    @GraphQLDescription("common field of animals")
    val type: AnimalType

    @GraphQLDescription("common function of animals")
    fun sound(): String
}

@GraphQLDescription("enum holding all supported animal types")
enum class AnimalType {
    CAT,
    DOG
}

@GraphQLDescription("dog is one of the implementations of animal")
class Dog : Animal {

    override val type: AnimalType
        get() = AnimalType.DOG

    override fun sound() = "bark"

    @GraphQLDescription("this is specific to dogs")
    fun doSomethingUseful(): String = "something useful"
}

@GraphQLDescription("cat is another implementation of animal")
class Cat : Animal {
    override val type: AnimalType
        get() = AnimalType.CAT

    override fun sound() = "meow"

    @GraphQLDescription("this is specific to cats")
    fun ignoreEveryone(): String = "ignore everyone"
}
