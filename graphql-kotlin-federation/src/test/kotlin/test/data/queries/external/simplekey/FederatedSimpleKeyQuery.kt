package test.data.queries.external.simplekey

import com.expedia.graphql.federation.directives.ExtendsDirective
import com.expedia.graphql.federation.directives.ExternalDirective
import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import test.data.queries.Query

class FederatedSimpleKeyQuery : Query {
    fun hello(name: String) = "hello $name"

    // override name for JUnit5 parameterized test
    override fun toString(): String = "@extended federated type with simple @key"
}

/*
type Product @extends @key(fields : "id") {
  description: String!
  id: String! @external
}
 */
@KeyDirective(fields = FieldSet("id"))
@ExtendsDirective
data class Product(@property:ExternalDirective val id: String, val description: String)
