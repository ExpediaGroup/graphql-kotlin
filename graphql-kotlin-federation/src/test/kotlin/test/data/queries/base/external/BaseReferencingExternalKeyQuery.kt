package test.data.queries.base.external

import com.expedia.graphql.federation.directives.ExternalDirective
import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import test.data.queries.Query

class BaseReferencingExternalKeyQuery : Query {
    fun product(id: String) = Product(id, "some product description")

    // override name for JUnit5 parameterized test
    override fun toString(): String = "base type referencing @external fields in @key"
}

/*
type Product @key(fields : "id") {
  description: String!
  id: String! @external
}
 */
@KeyDirective(fields = FieldSet("id"))
data class Product(@property:ExternalDirective val id: String, val description: String)
