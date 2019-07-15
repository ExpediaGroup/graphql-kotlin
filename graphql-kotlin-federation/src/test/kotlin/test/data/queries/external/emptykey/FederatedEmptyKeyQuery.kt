package test.data.queries.external.emptykey

import com.expedia.graphql.federation.directives.ExtendsDirective
import com.expedia.graphql.federation.directives.ExternalDirective
import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import test.data.queries.Query

class FederatedEmptyKeyQuery : Query {
    fun hello(name: String) = "hello $name"

    // override name for JUnit5 parameterized test
    override fun toString(): String = "@extended federated type with empty @key field set"
}

/*
type Product @extends @key(fields : "") {
  description: String!
  id: String! @external
}
 */
@KeyDirective(FieldSet(""))
@ExtendsDirective
data class Product(@property:ExternalDirective val id: String, val description: String)
