package test.data.queries.external.nokey

import com.expedia.graphql.annotations.GraphQLIgnore
import com.expedia.graphql.federation.directives.ExtendsDirective
import com.expedia.graphql.federation.directives.ExternalDirective
import test.data.queries.Query

class FederatedMissingKeyQuery : Query {
    fun hello(name: String) = "hello $name"

    // override name for JUnit5 parameterized test
    @GraphQLIgnore
    override fun toString(): String = "@external federated type without @key directive"
}

/*
type Product @extends {
  description: String!
  id: String! @external
}
 */
@ExtendsDirective
data class Product(@property:ExternalDirective val productId: String, val description: String)
