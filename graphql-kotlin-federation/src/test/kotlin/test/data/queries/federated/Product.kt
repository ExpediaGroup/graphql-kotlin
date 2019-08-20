package test.data.queries.federated

import com.expedia.graphql.annotations.GraphQLIgnore
import com.expedia.graphql.federation.directives.ExtendsDirective
import com.expedia.graphql.federation.directives.ExternalDirective
import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective
import com.expedia.graphql.federation.directives.ProvidesDirective
import com.expedia.graphql.federation.directives.RequiresDirective
import kotlin.properties.Delegates

/*
interface Product @extends @key(fields : "id") {
  id: String! @external
  reviews: [Review!]!
}
 */
@KeyDirective(fields = FieldSet("id"))
@ExtendsDirective
interface Product {
    @ExternalDirective val id: String
    fun reviews(): List<Review>
}

/*
type Book implements Product @extends @key(fields : "id") {
  author: User! @provides(fields : "name")
  id: String! @external
  reviews: [Review!]!
  shippingCost: String! @requires(fields : "weight")
  weight: Float! @external
}
 */
@ExtendsDirective
@KeyDirective(FieldSet("id"))
class Book(
    @property:ExternalDirective override val id: String
) : Product {

    // optionally provided as it is not part of the @key field set
    // will only be specified if federated query attempts to resolve shippingCost
    @ExternalDirective
    var weight: Double by Delegates.notNull()

    override fun reviews(): List<Review> = listOf(Review("parent-$id", "Dummy Review $id"))

    @RequiresDirective(FieldSet("weight"))
    fun shippingCost(): String = "$${weight * 9.99}"

    @ProvidesDirective(FieldSet("name"))
    fun author(): User = User(1, "John Doe")

    @Suppress("UnsafeCast")
    @GraphQLIgnore
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Book

        if (id != other.id) return false
        if (weight != other.weight) return false

        return true
    }

    @GraphQLIgnore
    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + weight.hashCode()
        return result
    }
}

/*
type Review {
  body: String!
  id: String!
}
 */
data class Review(val id: String, val body: String)

/*
type User {
  age: Int!
  name: String!
}
 */
@ExtendsDirective
@KeyDirective(FieldSet("userId"))
data class User(
    @property:ExternalDirective val userId: Int,
    @property:ExternalDirective val name: String
)
