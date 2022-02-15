package com.expediagroup.scalars.queries

import com.expediagroup.graphql.generator.scalars.ID
import com.ibm.icu.util.ULocale
import java.util.UUID

data class RequiredWrapper(
    val requiredBoolean: Boolean,
    val requiredDouble: Double,
    val requiredId: ID,
    val requiredInt: Int,
    val requiredIntList: List<Int>,
    val requiredObject: Simple,
    val requiredString: String,
    val requiredULocale: ULocale,
    val requiredUUID: UUID,
    val requiredUUIDList: List<UUID>
)
