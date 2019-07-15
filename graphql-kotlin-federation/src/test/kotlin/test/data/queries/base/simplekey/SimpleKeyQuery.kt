package test.data.queries.base.simplekey

import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import test.data.queries.Query

class SimpleKeyQuery : Query {
    fun product(id: String) = Product(id, "some product description")

    // override name for JUnit5 parameterized test
    override fun toString(): String = "base type with simple @key"
}

/*
type Product @key(fields : "id") {
  description: String!
  id: String!
}
 */
@KeyDirective(fields = FieldSet("id"))
data class Product(val id: String, val description: String)
