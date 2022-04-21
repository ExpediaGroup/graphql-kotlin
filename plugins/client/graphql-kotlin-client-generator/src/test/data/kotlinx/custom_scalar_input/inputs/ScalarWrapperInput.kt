package com.expediagroup.graphql.generated.inputs

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.client.serialization.serializers.OptionalScalarListSerializer
import com.expediagroup.graphql.client.serialization.serializers.OptionalScalarSerializer
import com.expediagroup.graphql.client.serialization.types.OptionalInput
import com.expediagroup.graphql.client.serialization.types.OptionalInput.Undefined
import com.expediagroup.graphql.generated.ID
import com.expediagroup.graphql.generated.scalars.OptionalUUIDListSerializer
import com.expediagroup.graphql.generated.scalars.OptionalUUIDSerializer
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
  @Serializable(with = OptionalScalarSerializer::class)
  public val count: OptionalInput<Int> = OptionalInput.Undefined,
  /**
   * Custom scalar of UUID
   */
  @Serializable(with = OptionalUUIDSerializer::class)
  public val custom: OptionalInput<@Serializable(with = UUIDSerializer::class) UUID> =
      OptionalInput.Undefined,
  /**
   * List of custom scalar UUIDs
   */
  @Serializable(with = OptionalUUIDListSerializer::class)
  public val customList: OptionalInput<List<@Serializable(with = UUIDSerializer::class) UUID>> =
      OptionalInput.Undefined,
  /**
   * ID represents unique identifier that is not intended to be human readable
   */
  public val id: ID,
  /**
   * Optional ID
   */
  @Serializable(with = OptionalScalarSerializer::class)
  public val optionalId: OptionalInput<ID> = OptionalInput.Undefined,
  /**
   * UTF-8 character sequence
   */
  public val name: String,
  /**
   * Optional list of names
   */
  @Serializable(with = OptionalScalarListSerializer::class)
  public val nameList: OptionalInput<List<String>> = OptionalInput.Undefined,
  /**
   * A nullable signed double-precision floating-point value
   */
  @Serializable(with = OptionalScalarSerializer::class)
  public val rating: OptionalInput<Double> = OptionalInput.Undefined,
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
  public val listLocale: List<@Serializable(with = ULocaleSerializer::class) ULocale>,
)
