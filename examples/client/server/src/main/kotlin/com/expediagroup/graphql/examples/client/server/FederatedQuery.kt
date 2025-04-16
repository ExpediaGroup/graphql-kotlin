package com.expediagroup.graphql.examples.client.server

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.federation.directives.ExternalDirective
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.InaccessibleDirective
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.federation.directives.OverrideDirective
import com.expediagroup.graphql.generator.federation.directives.ProvidesDirective
import com.expediagroup.graphql.generator.federation.directives.ShareableDirective
import com.expediagroup.graphql.generator.federation.directives.TagDirective
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeSuspendResolver
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component

@Component
class FederatedQuery : Query {

    fun product(id: ID) = Product.byID(id)
}

@KeyDirective(fields = FieldSet("id"))
// @KeyDirective(fields = FieldSet("sku package"))
// @KeyDirective(fields = FieldSet("sku variation { id }"))
data class Product(
    val id: ID,
    val sku: String? = null,
    @GraphQLName("package")
    val pkg: String? = null,
    val variation: ProductVariation? = null,
    val dimensions: ProductDimension? = null,
    @ProvidesDirective(FieldSet("totalProductsCreated"))
    val createdBy: User? = null,
    @TagDirective("internal")
    val notes: String? = null
) {
    companion object {
        fun byID(id: ID) = PRODUCTS.find { it.id.value == id.value }
        private fun bySkuAndPackage(sku: String, pkg: String) = PRODUCTS.find { it.sku == sku && it.pkg == pkg }
        private fun bySkuAndVariation(sku: String, variationId: String) =
            PRODUCTS.find { it.sku == sku && it.variation?.id?.value == variationId }

        fun byReference(ref: Map<String, Any>): Product? {
            val id = ref["id"]?.toString()
            val sku = ref["sku"]?.toString()
            val pkg = ref["package"]?.toString()
            val variation = ref["variation"]
            val variationId = if (variation is Map<*, *>) {
                variation["id"].toString()
            } else null

            return when {
                id != null -> byID(ID(id))
                sku != null && pkg != null -> bySkuAndPackage(sku, pkg)
                sku != null && variationId != null -> bySkuAndVariation(sku, variationId)
                else -> throw RuntimeException("invalid entity reference")
            }
        }
    }
}

val PRODUCTS = listOf(
    Product(
        ID("apollo-federation"),
        "federation",
        "@apollo/federation",
        ProductVariation(ID("OSS")),
        ProductDimension("small", 1.0f),
        User(email = "support@apollographql.com", name = "support", totalProductsCreated = 1337)
    ),
    Product(
        ID("apollo-studio"),
        "studio",
        "",
        ProductVariation(ID("platform")),
        ProductDimension("small", 1.0f),
        User(email = "support@apollographql.com", name = "support", totalProductsCreated = 1337)
    )
)

@ShareableDirective
data class ProductDimension(
    val size: String? = null,
    val weight: Float? = null,
    @InaccessibleDirective
    val unit: String? = null
)

data class ProductVariation(
    val id: ID
)

@KeyDirective(fields = FieldSet("email"))
data class User(
    @ExternalDirective
    val email: String,
    @OverrideDirective(from = "users")
    val name: String,
    @ExternalDirective
    val totalProductsCreated: Int? = null
)

@Component
class ProductsResolver : FederatedTypeSuspendResolver<Product> {
    override val typeName: String = "Product"

    override suspend fun resolve(
        environment: DataFetchingEnvironment,
        representation: Map<String, Any>
    ): Product? = Product.byReference(representation)
}

@Component
class UserResolver : FederatedTypeSuspendResolver<User> {
    override val typeName: String = "User"

    override suspend fun resolve(
        environment: DataFetchingEnvironment,
        representation: Map<String, Any>
    ): User? {
        val email = representation["email"]?.toString() ?: throw RuntimeException("invalid entity reference")
        return User(email = email, name = "default", totalProductsCreated = 1337)
    }
}
