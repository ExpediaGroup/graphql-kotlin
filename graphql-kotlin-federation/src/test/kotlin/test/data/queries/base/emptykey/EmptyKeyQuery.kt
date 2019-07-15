package test.data.queries.base.emptykey

import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import test.data.queries.Query

class EmptyKeyQuery : Query {
    fun product(id: String) = Product(id, "some product description")

    // override name for JUnit5 parameterized test
    override fun toString(): String = "base type with empty @key field set"
}

/*
type Product @key(fields : "") {
  description: String!
  id: String!
}
 */
@KeyDirective(FieldSet(""))
data class Product(val id: String, val description: String)
