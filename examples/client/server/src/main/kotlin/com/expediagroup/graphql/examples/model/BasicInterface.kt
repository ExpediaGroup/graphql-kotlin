package com.expediagroup.graphql.examples.model

import com.expediagroup.graphql.annotations.GraphQLDescription

@GraphQLDescription("Very basic interface")
interface BasicInterface {
    @GraphQLDescription("Unique identifier of an interface")
    val id: Int
    @GraphQLDescription("Name field")
    val name: String
}

@GraphQLDescription("Example interface implementation where value is an integer")
data class FirstInterfaceImplementation(
    @GraphQLDescription("Unique identifier of the first implementation")
    override val id: Int,
    @GraphQLDescription("Name of the first implementation")
    override val name: String,
    @GraphQLDescription("Custom field integer value")
    val intValue: Int
) : BasicInterface

@GraphQLDescription("Example interface implementation where value is a float")
data class SecondInterfaceImplementation(
    @GraphQLDescription("Unique identifier of the second implementation")
    override val id: Int,
    @GraphQLDescription("Name of the second implementation")
    override val name: String,
    @GraphQLDescription("Custom field float value")
    val floatValue: Float
) : BasicInterface
