package com.expediagroup.federation.compatibility

import com.expediagroup.federation.compatibility.model.DeprecatedProduct
import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class DeprecatedProductQuery : Query {
    @Deprecated("Use product query instead")
    fun deprecatedProduct(sku: String, `package`: String) = DeprecatedProduct.bySkuAndPackage(sku, `package`)
}
