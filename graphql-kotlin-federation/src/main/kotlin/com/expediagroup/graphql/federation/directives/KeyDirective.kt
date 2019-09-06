package com.expediagroup.graphql.federation.directives

import com.expediagroup.graphql.annotations.GraphQLDirective
import graphql.introspection.Introspection

/**
 * ```graphql
 * directive @key(fields: _FieldSet!) on OBJECT | INTERFACE
 * ```
 *
 * The @key directive is used to indicate a combination of fields that can be used to uniquely identify and fetch an object or interface. Key directive should be specified on the root base type as
 * well as all the corresponding federated (i.e. extended) types. Key fields specified in the directive field set should correspond to a valid field on the underlying GraphQL interface/object.
 * Federated extended types should also instrument all the referenced key fields with @external directive.
 *
 * NOTE: federation spec specifies that multiple @key directives can be applied on the field which is at odds with graphql-spec and currently unsupported by graphql-kotlin.
 *
 * Example:
 * Given
 *
 * ```kotlin
 * @KeyDirective(FieldSet("id"))
 * class Product(val id: String, val name: String)
 * ```
 *
 * should generate
 *
 * ```graphql
 * type Product @key(fields: "id") {
 *   id: String!
 *   name: String!
 * }
 * ```
 *
 * @param fields field set that represents a set of fields forming the key
 *
 * @see FieldSet
 * @see com.expediagroup.graphql.federation.types.FIELD_SET_SCALAR_TYPE
 * @see ExtendsDirective
 * @see ExternalDirective
 */
@GraphQLDirective(
    name = "key",
    description = "Space separated list of primary keys needed to access federated object",
    locations = [Introspection.DirectiveLocation.OBJECT, Introspection.DirectiveLocation.INTERFACE]
)
annotation class KeyDirective(val fields: FieldSet)
