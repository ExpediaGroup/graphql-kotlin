package com.expediagroup.graphql.generated.unionsameselections

import com.expediagroup.graphql.client.Generated
import com.fasterxml.jackson.`annotation`.JsonProperty
import com.fasterxml.jackson.`annotation`.JsonSubTypes
import com.fasterxml.jackson.`annotation`.JsonTypeInfo
import com.fasterxml.jackson.`annotation`.JsonTypeInfo.As.PROPERTY
import com.fasterxml.jackson.`annotation`.JsonTypeInfo.Id.NAME
import kotlin.String

@Generated
@JsonTypeInfo(
  use = JsonTypeInfo.Id.NAME,
  include = JsonTypeInfo.As.PROPERTY,
  property = "__typename",
  defaultImpl = DefaultProductRatingSupportingMessageImplementation::class,
)
@JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    ProductRatingLink::class,
    name="ProductRatingLink"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    EGDSPlainText::class, name="EGDSPlainText")])
public interface ProductRatingSupportingMessage

@Generated
public data class ProductRatingLink(
  @get:JsonProperty(value = "link")
  public val link: EGDSStandardLink,
  @get:JsonProperty(value = "action")
  public val action: EGDSProductRatingShowTextAction,
) : ProductRatingSupportingMessage

@Generated
public data class EGDSPlainText(
  @get:JsonProperty(value = "text")
  public val text: String,
) : ProductRatingSupportingMessage,
    ProductSupportingMessage

/**
 * Fallback ProductRatingSupportingMessage implementation that will be used when unknown/unhandled
 * type is encountered.
 */
@Generated
public class DefaultProductRatingSupportingMessageImplementation() : ProductRatingSupportingMessage
