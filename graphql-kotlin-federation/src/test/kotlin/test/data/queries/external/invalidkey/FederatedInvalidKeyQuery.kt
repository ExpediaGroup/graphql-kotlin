package test.data.queries.external.invalidkey

import com.expedia.graphql.federation.directives.ExtendsDirective
import com.expedia.graphql.federation.directives.ExternalDirective
import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import test.data.queries.Query

class FederatedInvalidKeyQuery : Query {
    fun hello(name: String) = "hello $name"

    // override name for JUnit5 parameterized test
    override fun toString(): String = "@extend federated type with non-existent @key"
}

/*
type Product @extends @key(fields : "id") {
  description: String!
  productId: String! @external
}
 */
@KeyDirective(fields = FieldSet("id"))
@ExtendsDirective
data class Product(@property:ExternalDirective val productId: String, val description: String)
