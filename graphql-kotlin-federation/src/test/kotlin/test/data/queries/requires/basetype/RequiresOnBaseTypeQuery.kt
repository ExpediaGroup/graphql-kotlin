package test.data.queries.requires.basetype

import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import com.expedia.graphql.federation.directives.RequiresDirective
import test.data.queries.Query

class RequiresOnBaseTypeQuery : Query {
    fun product(id: String) = Product(id, "some product description")

    // override name for JUnit5 parameterized test
    override fun toString(): String = "@requires directive specified on base type"
}

/*
type Product @key(fields : "id") {
  description: String!
  id: String!
  shippingCost: String! @requires(fields : "weight")
  weight: Float!
}
 */
@KeyDirective(fields = FieldSet("id"))
class Product(val id: String, val description: String) {
    var weight: Double = 0.0

    @RequiresDirective(FieldSet("weight"))
    fun shippingCost(): String = "$${weight * 9.99}"
}
