package com.expediagroup.federation.compatibility.model

import com.expediagroup.graphql.generator.federation.directives.InaccessibleDirective
import com.expediagroup.graphql.generator.federation.directives.ShareableDirective

/*
type ProductDimension @shareable {
  size: String
  weight: Float
  unit: String @inaccessible
}
 */
@ShareableDirective
data class ProductDimension(
    val size: String? = null,
    val weight: Float? = null,
    @InaccessibleDirective
    val unit: String? = null
)
