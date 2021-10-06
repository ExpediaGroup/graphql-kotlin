package com.expediagroup.scalars

import com.expediagroup.graphql.generator.scalars.ID
import com.ibm.icu.util.ULocale
import java.util.UUID

/** Wrapper that holds all supported scalar types */
data class ScalarWrapper(
    /** A signed 32-bit nullable integer value */
    val count: Int? = null,
    /** ID represents unique identifier that is not intended to be human readable */
    val id: ID,
    /** UTF-8 character sequence */
    val name: String,
    /** Custom scalar of Locale */
    val locale: ULocale,
    /** List of custom scalar Locales */
    val localeList: List<ULocale>,
    /** A nullable signed double-precision floating-point value */
    val rating: Double? = null,
    /** Custom scalar of UUID */
    val uuid: UUID? = null,
    /** List of custom scalar UUIDs */
    val uuidList: List<UUID>? = null,
    /** Either true or false */
    val valid: Boolean = true
)
