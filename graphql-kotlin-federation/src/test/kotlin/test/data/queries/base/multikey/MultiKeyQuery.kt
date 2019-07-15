package test.data.queries.base.multikey

import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import test.data.queries.Query

class MultiKeyQuery : Query {
    fun product(id: String) = Product(id, "secondary key", "some product description")

    // override name for JUnit5 parameterized test
    override fun toString(): String = "base type with @key referencing multiple fields"
}

/*
type Product @key(fields : "id type") {
  description: String!
  id: String!
  type: String!
}
 */
@KeyDirective(fields = FieldSet("id type"))
data class Product(val id: String, val type: String, val description: String)
