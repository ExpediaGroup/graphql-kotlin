package test.data.queries.base.nestedkey

import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import test.data.queries.Query

class NestedKeyQuery : Query {
    fun product(id: String) = Product(NestedId(id), "some product description")

    // override name for JUnit5 parameterized test
    override fun toString(): String = "base type with @key referencing nested fields"
}

/*
type Product @key(fields : "id { uuid }") {
  description: String!
  id: NestedId!
}
 */
@KeyDirective(fields = FieldSet("id { uuid }"))
data class Product(val id: NestedId, val description: String)

/*
type NestedId {
  uuid: String!
}
 */
data class NestedId(val uuid: String)
