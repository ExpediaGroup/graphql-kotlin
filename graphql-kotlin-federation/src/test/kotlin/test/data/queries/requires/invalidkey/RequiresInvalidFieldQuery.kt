package test.data.queries.requires.invalidkey

import com.expedia.graphql.federation.directives.ExtendsDirective
import com.expedia.graphql.federation.directives.ExternalDirective
import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import com.expedia.graphql.federation.directives.RequiresDirective
import test.data.queries.Query
import kotlin.properties.Delegates

class RequiresInvalidFieldQuery : Query {
    fun hello(name: String) = "hello $name"

    // override name for JUnit5 parameterized test
    override fun toString(): String = "@requires directive references non-existent fields"
}

/*
type Product @extends @key(fields : "id") {
  description: String!
  id: String! @external
  shippingCost: String! @requires(fields : "weight")
  weight: Float! @external
}
 */
@KeyDirective(fields = FieldSet("id"))
@ExtendsDirective
class Product(@property:ExternalDirective val id: String, val description: String) {

    @ExternalDirective
    var weight: Double by Delegates.notNull()

    @RequiresDirective(FieldSet("zipCode"))
    fun shippingCost(): String = "$${weight * 9.99}"
}
