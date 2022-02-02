package com.expediagroup.scalars.queries

import com.expediagroup.graphql.generator.scalars.ID
import com.ibm.icu.util.ULocale
import java.util.UUID

const val UNDEFINED_BOOLEAN = false
const val UNDEFINED_DOUBLE = Double.MIN_VALUE
const val UNDEFINED_INT = Int.MIN_VALUE
const val UNDEFINED_STRING = "undefined"
val UNDEFINED_LOCALE = ULocale.US
val UNDEFINED_OBJECT = Simple(foo = "bar")
val UNDEFINED_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

data class OptionalWrapper(
    val optionalBoolean: Boolean? = UNDEFINED_BOOLEAN,
    val optionalDouble: Double? = UNDEFINED_DOUBLE,
    val optionalId: ID? = ID(UNDEFINED_STRING),
    val optionalInt: Int? = UNDEFINED_INT,
    val optionalIntList: List<Int>? = emptyList(),
    val optionalObject: Simple? = UNDEFINED_OBJECT,
    val optionalString: String? = UNDEFINED_STRING,
    val optionalULocale: ULocale? = UNDEFINED_LOCALE,
    val optionalUUID: UUID? = UNDEFINED_UUID,
    val optionalUUIDList: List<UUID>? = emptyList()
)
