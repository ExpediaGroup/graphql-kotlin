package test.data.queries.external.basetype

import com.expedia.graphql.federation.directives.ExtendsDirective
import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import test.data.queries.Query

class ExtensionReferencingLocalKeyQuery : Query {
    fun hello(name: String) = "hello $name"

    // override name for JUnit5 parameterized test
    override fun toString(): String = "@extend federated type referencing local @key"
}

/*
type Product @extends @key(fields : "id") {
  description: String!
  id: String!
}
 */
@KeyDirective(fields = FieldSet("id"))
@ExtendsDirective
data class Product(val id: String, val description: String)
