package test.data.queries.provides.localtype

import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import com.expedia.graphql.federation.directives.ProvidesDirective
import test.data.queries.Query

class ProvidesOnBaseTypeQuery : Query {
    fun product(id: String) = Product(id, "some product description")

    // override name for JUnit5 parameterized test
    override fun toString(): String = "@provides references fields from local base type"
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
type Review {
  reviewId: String!
  text: String!
}
 */
data class Review(
    val reviewId: String,
    val text: String
)
