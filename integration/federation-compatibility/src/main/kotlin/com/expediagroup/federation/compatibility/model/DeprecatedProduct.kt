package com.expediagroup.federation.compatibility.model

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeSuspendResolver
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

val DEPRECATED_PRODUCT = DeprecatedProduct("apollo-federation-v1", "@apollo/federation-v1", "Migrate to Federation V2")

/*
type DeprecatedProduct @key(fields: "sku package") {
  sku: String!
  package: String!
  reason: String
  createdBy: User
}
 */
@KeyDirective(fields = FieldSet("sku package"))
data class DeprecatedProduct(
    val sku: String,
    @GraphQLName("package")
    val pkg: String,
    val reason: String?,
    val createdBy: User? = DEFAULT_USER
) {
    companion object {
        fun bySkuAndPackage(sku: String, pkg: String): DeprecatedProduct? =
            if (DEPRECATED_PRODUCT.sku == sku && DEPRECATED_PRODUCT.pkg == pkg) {
                DEPRECATED_PRODUCT
            } else {
                null
            }

        fun byReference(ref: Map<String, Any>): DeprecatedProduct? {
            val sku = ref["sku"]?.toString()
            val pkg = ref["package"]?.toString()
            return when {
                sku != null && pkg != null -> bySkuAndPackage(sku, pkg)
                else -> throw RuntimeException("invalid entity reference")
            }
        }
    }
}

@Component
class DeprecatedProductResolver : FederatedTypeSuspendResolver<DeprecatedProduct> {
    override val typeName: String = "DeprecatedProduct"

    override suspend fun resolve(
        environment: DataFetchingEnvironment,
        representation: Map<String, Any>
    ): DeprecatedProduct? = DeprecatedProduct.byReference(representation)
}
