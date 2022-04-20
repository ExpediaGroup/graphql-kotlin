package com.expediagroup.graphql.generated.customscalarquery

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.generated.ID
import com.expediagroup.graphql.generated.scalars.ULocaleSerializer
import com.expediagroup.graphql.generated.scalars.UUIDSerializer
import com.ibm.icu.util.ULocale
import java.util.UUID
import kotlin.collections.List
import kotlinx.serialization.Serializable

/**
 * Wrapper that holds all supported scalar types
 */
@Generated
@Serializable
public data class ScalarWrapper(
  /**
   * ID represents unique identifier that is not intended to be human readable
   */
  public val id: ID,
  /**
   * Custom scalar of UUID
   */
  @Serializable(with = UUIDSerializer::class)
  public val custom: UUID?,
  /**
   * List of custom scalar UUIDs
   */
  public val customList: List<@Serializable(with = UUIDSerializer::class) UUID>?,
  /**
   * Custom scalar of Locale
   */
  @Serializable(with = ULocaleSerializer::class)
  public val locale: ULocale,
  /**
   * List of custom scalar Locales
   */
  public val listLocale: List<@Serializable(with = ULocaleSerializer::class) ULocale>,
)
