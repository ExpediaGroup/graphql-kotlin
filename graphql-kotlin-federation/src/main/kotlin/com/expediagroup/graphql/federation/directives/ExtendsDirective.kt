package com.expediagroup.graphql.federation.directives

import com.expediagroup.graphql.annotations.GraphQLDirective
import graphql.introspection.Introspection

private const val NAME = "extends"
private const val DESCRIPTION = "Marks target object as extending part of the federated schema"

/**
 * ```graphql
 * directive @extends on OBJECT | INTERFACE
 * ```
 *
 * Extends directive is used to represent type extensions in the schema. Native type extensions are currently unsupported by the graphql-kotlin libraries. Federated extended types should have
 * corresponding @key directive defined that specifies primary key required to fetch the underlying object.
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
 * @see KeyDirective
 */
@GraphQLDirective(
    name = NAME,
    description = DESCRIPTION,
    locations = [Introspection.DirectiveLocation.OBJECT, Introspection.DirectiveLocation.INTERFACE]
)
annotation class ExtendsDirective

val extendsDirectiveType: graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(NAME)
    .description(DESCRIPTION)
    .validLocations(Introspection.DirectiveLocation.OBJECT, Introspection.DirectiveLocation.INTERFACE)
    .build()
