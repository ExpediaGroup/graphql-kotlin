package test.data.queries.base.invalidkey

import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import test.data.queries.Query

class InvalidKeyQuery : Query {
    fun product(id: String) = Product(id, "some product description")

    // override name for JUnit5 parameterized test
    override fun toString(): String = "base type references non-existent @key fields"
}

/*
type Product @key(fields : "id") {
  description: String!
  productId: String!
}
 */
@KeyDirective(fields = FieldSet("id"))
data class Product(val productId: String, val description: String)
