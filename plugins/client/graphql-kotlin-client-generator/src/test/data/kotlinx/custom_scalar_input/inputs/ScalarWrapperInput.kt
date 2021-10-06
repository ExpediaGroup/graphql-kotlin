package com.expediagroup.graphql.generated.inputs

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.generated.ID
import com.expediagroup.graphql.generated.scalars.ULocaleSerializer
import com.expediagroup.graphql.generated.scalars.UUIDSerializer
import com.ibm.icu.util.ULocale
import java.util.UUID
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.String
import kotlin.collections.List
import kotlinx.serialization.Serializable

/**
 * Wrapper that holds all supported scalar types
 */
@Generated
@Serializable
public data class ScalarWrapperInput(
  /**
   * A signed 32-bit nullable integer value
   */
  public val count: Int? = null,
  /**
   * Custom scalar of UUID
   */
  @Serializable(with = UUIDSerializer::class)
  public val custom: UUID? = null,
  /**
   * List of custom scalar UUIDs
   */
  public val customList: List<@Serializable(with = UUIDSerializer::class) UUID>? = null,
  /**
   * ID represents unique identifier that is not intended to be human readable
   */
  public val id: ID,
  /**
   * UTF-8 character sequence
   */
  public val name: String,
  /**
   * A nullable signed double-precision floating-point value
   */
  public val rating: Double? = null,
  /**
   * Either true or false
   */
  public val valid: Boolean,
  /**
   * Custom scalar of Locale
   */
  @Serializable(with = ULocaleSerializer::class)
  public val locale: ULocale,
  /**
   * List of custom scalar Locales
   */
  public val listLocale: List<@Serializable(with = ULocaleSerializer::class) ULocale>
)
