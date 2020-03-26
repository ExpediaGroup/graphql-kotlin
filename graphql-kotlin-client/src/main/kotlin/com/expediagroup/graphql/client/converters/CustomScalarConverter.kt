package com.expediagroup.graphql.client.converters

interface CustomScalarConverter<T> {
    fun toScalar(rawValue: String): T
    fun toJson(value: T): String
}
