package test.data.queries.external.nestedkey

import com.expedia.graphql.federation.directives.ExtendsDirective
import com.expedia.graphql.federation.directives.ExternalDirective
import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import test.data.queries.Query

class FederatedNestedKeyQuery : Query {
    fun hello(name: String) = "hello $name"

    // override name for JUnit5 parameterized test
    override fun toString(): String = "@extends federated type with @key referencing nested fields"
}

/*
type Product @extends @key(fields : "id { uuid }") {
  description: String!
  id: NestedId! @external
}
 */
@KeyDirective(fields = FieldSet("id { uuid }"))
@ExtendsDirective
data class Product(@property:ExternalDirective val id: NestedId, val description: String)

/*
type NestedId @extends @key(fields : "uuid") {
  uuid: String! @external
}
 */
@KeyDirective(fields = FieldSet("uuid"))
@ExtendsDirective
data class NestedId(@property:ExternalDirective val uuid: String)
