/*
 * Copyright 2022 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.generator.federation.data.queries.federated.v2

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import com.expediagroup.graphql.generator.annotations.GraphQLIgnore
import com.expediagroup.graphql.generator.federation.directives.ExternalDirective
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.federation.directives.ProvidesDirective
import com.expediagroup.graphql.generator.federation.directives.RequiresDirective
import kotlin.properties.Delegates

/*
interface Product @key(fields : "id") @key(fields : "upc") {
  id: String!
  upc: String!
  reviews: [Review!]!
}
 */
@KeyDirective(fields = FieldSet("id"))
@KeyDirective(fields = FieldSet("upc"))
interface Product {
    val id: String
    val upc: String
    fun reviews(): List<Review>
}

/*
type Book implements Product @key(fields : "id") @key(fields : "upc") {
  author: User! @provides(fields : "name")
  id: String!
  upc: String!
  reviews: [Review!]!
  shippingCost: String! @requires(fields : "weight")
  weight: Float! @external
}
 */
@KeyDirective(FieldSet("id"))
@KeyDirective(FieldSet("upc"))
class Book(
    override val id: String,
    override val upc: String
) : Product {

    constructor(id: String) : this(id, id)

    // optionally provided as it is not part of the @key field set
    // will only be specified if federated query attempts to resolve shippingCost
    @ExternalDirective
    var weight: Double by Delegates.notNull()

    override fun reviews(): List<Review> = listOf(Review(id = "parent-$id", body = "Dummy Review $id", content = null, customScalar = CustomScalar("foo")))

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
  body: String! @custom
  customScalar: CustomScalar @deprecated(reason: "no longer supported, use Review.body instead")
  id: String!
}
 */
data class Review(
    val id: String,
    @CustomDirective val body: String,
    @Deprecated(message = "no longer supported", replaceWith = ReplaceWith("use Review.body instead")) val content: String? = null,
    val customScalar: CustomScalar
)

/*
type User @key(fields: "userId") {
  age: Int!
  name: String!
}
 */
@KeyDirective(FieldSet("userId"))
data class User(
    val userId: Int,
    val name: String
)

/*
type Author @key(fields: "authorId") {
  authorId: Int!
  name: String!
}
 */
@KeyDirective(FieldSet("authorId"))
data class Author(
    val authorId: Int,
    val name: String
)

@GraphQLDirective(name = "custom")
@GraphQLDescription(
    """
    This is a multi-line comment on a custom directive.
    This should still work multiline and double quotes (") in the description.
    Line 3.
    """
)
annotation class CustomDirective

class CustomScalar(val value: String)
