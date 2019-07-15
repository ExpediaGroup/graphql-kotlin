package test.data.queries.external.multikey

import com.expedia.graphql.federation.directives.ExtendsDirective
import com.expedia.graphql.federation.directives.ExternalDirective
import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import test.data.queries.Query

class FederatedMultiKeyQuery : Query {
    fun product(id: String) = Product(id, "secondary key", "some product description")

    // override name for JUnit5 parameterized test
    override fun toString(): String = "@extends federated type with @key referencing multiple fields"
}

/*
type Product @extends @key(fields : "id type") {
  description: String!
  id: String! @external
  type: String! @external
}
 */
@KeyDirective(fields = FieldSet("id type"))
@ExtendsDirective
data class Product(@property:ExternalDirective val id: String, @property:ExternalDirective val type: String, val description: String)
