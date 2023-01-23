package com.expediagroup.scalars.queries

import com.expediagroup.graphql.generator.annotations.GraphQLValidObjectLocations
import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.generator.scalars.ID
import com.ibm.icu.util.ULocale
import java.util.UUID

const val UNDEFINED_BOOLEAN = false
const val UNDEFINED_DOUBLE = Double.MIN_VALUE
const val UNDEFINED_INT = Int.MIN_VALUE
const val UNDEFINED_STRING = "undefined"
val UNDEFINED_LOCALE: ULocale = ULocale.US
val UNDEFINED_OBJECT: Simple = Simple(foo = "bar")
val UNDEFINED_UUID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000000")

@GraphQLValidObjectLocations(locations = [GraphQLValidObjectLocations.Locations.INPUT_OBJECT])
data class OptionalWrapperInput(
    val optionalBoolean: OptionalInput<Boolean>? = OptionalInput.Defined(UNDEFINED_BOOLEAN),
    val optionalDouble: OptionalInput<Double>? = OptionalInput.Defined(UNDEFINED_DOUBLE),
    val optionalId: OptionalInput<ID>? = OptionalInput.Defined(ID(UNDEFINED_STRING)),
    val optionalInt: OptionalInput<Int>? = OptionalInput.Defined(UNDEFINED_INT),
    val optionalIntList: OptionalInput<List<Int>>? = OptionalInput.Defined(emptyList()),
    val optionalObject: OptionalInput<Simple>? = OptionalInput.Defined(UNDEFINED_OBJECT),
    val optionalString: OptionalInput<String>? = OptionalInput.Defined(UNDEFINED_STRING),
    val optionalULocale: OptionalInput<ULocale>? = OptionalInput.Defined(UNDEFINED_LOCALE),
    val optionalUUID: OptionalInput<UUID>? = OptionalInput.Defined(UNDEFINED_UUID),
    val optionalUUIDList: OptionalInput<List<UUID>>? = OptionalInput.Defined(emptyList())
) {
    fun toOptionalWrapper(): OptionalWrapper = OptionalWrapper(
        optionalBoolean = optionalBoolean?.valueOrNull(UNDEFINED_BOOLEAN),
        optionalDouble = optionalDouble?.valueOrNull(UNDEFINED_DOUBLE),
        optionalId = optionalId?.valueOrNull(ID(UNDEFINED_STRING)),
        optionalInt = optionalInt?.valueOrNull(UNDEFINED_INT),
        optionalIntList = optionalIntList?.valueOrNull(emptyList()),
        optionalObject = optionalObject?.valueOrNull(UNDEFINED_OBJECT),
        optionalString = optionalString?.valueOrNull(UNDEFINED_STRING),
        optionalULocale = optionalULocale?.valueOrNull(UNDEFINED_LOCALE),
        optionalUUID = optionalUUID?.valueOrNull(UNDEFINED_UUID),
        optionalUUIDList = optionalUUIDList?.valueOrNull(emptyList())
    )

    private inline fun <reified T> OptionalInput<T>.valueOrNull(default: T): T? = when(this) {
        is OptionalInput.Defined -> this.value
        else -> default
    }
}

@GraphQLValidObjectLocations(locations = [GraphQLValidObjectLocations.Locations.OBJECT])
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
