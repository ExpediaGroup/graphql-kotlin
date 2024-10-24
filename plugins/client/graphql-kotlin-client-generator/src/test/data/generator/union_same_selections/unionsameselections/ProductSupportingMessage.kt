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
  defaultImpl = DefaultProductSupportingMessageImplementation::class,
)
@JsonSubTypes(value = [com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    EGDSParagraph::class,
    name="EGDSParagraph"),com.fasterxml.jackson.annotation.JsonSubTypes.Type(value =
    EGDSPlainText::class, name="EGDSPlainText")])
public interface ProductSupportingMessage

@Generated
public data class EGDSParagraph(
  @get:JsonProperty(value = "text")
  public val text: String,
) : ProductSupportingMessage

/**
 * Fallback ProductSupportingMessage implementation that will be used when unknown/unhandled type is
 * encountered.
 */
@Generated
public class DefaultProductSupportingMessageImplementation() : ProductSupportingMessage
