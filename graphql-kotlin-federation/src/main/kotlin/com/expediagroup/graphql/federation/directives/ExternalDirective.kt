package com.expediagroup.graphql.federation.directives

import com.expediagroup.graphql.annotations.GraphQLDirective
import graphql.introspection.Introspection

/**
 * ```graphql
 * directive @external on FIELD_DEFINITION
 * ```
 *
 * The @external directive is used to mark a field as owned by another service. This allows service A to use fields from service B while also knowing at runtime the types of that field. @external
 * directive is only applicable on federated extended types. All the external fields should either be referenced from the @key, @requires or @provides directives field sets.
 *
 * Example:
 * Given
 *
 * ```kotlin
 * @KeyDirective(FieldSet("id"))
 * @ExtendsDirective
 * class Product(@property:ExternalDirective val id: String) {
 *   fun newFunctionality(): String = "whatever"
 * }
 * ```
 *
 * should generate
 *
 * ```graphql
 * type Product @extends @key(fields : "id") {
 *   id: String! @external
 *   newFunctionality: String!
 * }
 * ```
 *
 * @see ExtendsDirective
 * @see KeyDirective
 * @see RequiresDirective
 */
@GraphQLDirective(
    name = "external",
    description = "Marks target field as external meaning it will be resolved by federated schema",
    locations = [Introspection.DirectiveLocation.FIELD_DEFINITION]
)
annotation class ExternalDirective
