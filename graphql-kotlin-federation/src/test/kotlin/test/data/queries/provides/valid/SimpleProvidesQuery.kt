package test.data.queries.provides.valid

import com.expedia.graphql.federation.directives.ExtendsDirective
import com.expedia.graphql.federation.directives.ExternalDirective
import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import com.expedia.graphql.federation.directives.ProvidesDirective
import test.data.queries.Query

class SimpleProvidesQuery : Query {
    fun product(id: String) = Product(id, "some product description")

    // override name for JUnit5 parameterized test
    override fun toString(): String = "@provides references valid fields"
}

/*
type Product @key(fields : "id") {
  description: String!
  id: String!
  topReview: Review! @provides(fields : "text")
}
 */
@KeyDirective(fields = FieldSet("id"))
class Product(val id: String, val description: String) {

    @ProvidesDirective(fields = FieldSet("text"))
    fun topReview() = Review("123", "some text")
}

/*
type Review @extends @key(fields : "reviewId") {
  reviewId: String! @external
  text: String! @external
}
 */
@KeyDirective(fields = FieldSet("reviewId"))
@ExtendsDirective
data class Review(
    @property:ExternalDirective val reviewId: String,
    @property:ExternalDirective val text: String
)
