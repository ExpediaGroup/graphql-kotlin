package com.expediagroup.graphql.client.jackson.data.scalars

import com.expediagroup.graphql.client.converter.ScalarConverter
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class UUID(
    val value: java.util.UUID
) {
    @JsonValue
    fun rawValue() = converter.toJson(value)

    companion object {
        val converter: UUIDScalarConverter = UUIDScalarConverter()

        @JsonCreator
        @JvmStatic
        fun create(rawValue: Any) = UUID(converter.toScalar(rawValue))
    }
}

// scalar converter would not be part of the generated sources
class UUIDScalarConverter : ScalarConverter<java.util.UUID> {
    override fun toScalar(rawValue: Any): java.util.UUID = java.util.UUID.fromString(rawValue.toString())
    override fun toJson(value: java.util.UUID): String = value.toString()
}
