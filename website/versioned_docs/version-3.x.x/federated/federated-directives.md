---
id: federated-directives
title: Federated Directives
original_id: federated-directives
---
`graphql-kotlin` supports a number of directives that can be used to annotate a schema and direct certain behaviors.

## `@extends` directive

```graphql

directive @extends on OBJECT | INTERFACE

```

`@extends` directive is used to represent type extensions in the schema. Native type extensions are currently
unsupported by the `graphql-kotlin` libraries. Federated extended types should have corresponding `@key` directive
defined that specifies primary key required to fetch the underlying object.

Example

```kotlin

@KeyDirective(FieldSet("id"))
@ExtendsDirective
class Product(@property:ExternalDirective val id: String) {
   fun newFunctionality(): String = "whatever"
}

```

will generate

```graphql

type Product @extends @key(fields : "id") {
  id: String! @external
  newFunctionality: String!
}

```

## `@external` directive

```graphql

directive @external on FIELD_DEFINITION

```

The `@external` directive is used to mark a field as owned by another service. This allows service A to use fields from
service B while also knowing at runtime the types of that field. `@external` directive is only applicable on federated
extended types. All the external fields should either be referenced from the `@key`, `@requires` or `@provides`
directives field sets.

Example

```kotlin

@KeyDirective(FieldSet("id"))
@ExtendsDirective
class Product(@property:ExternalDirective val id: String) {
  fun newFunctionality(): String = "whatever"
}

```

will generate

```graphql

type Product @extends @key(fields : "id") {
  id: String! @external
  newFunctionality: String!
}

```

## `@key` directive

```graphql

directive @key(fields: _FieldSet!) on OBJECT | INTERFACE

```

The `@key` directive is used to indicate a combination of fields that can be used to uniquely identify and fetch an
object or interface. Specified field set can represent single field (e.g. `"id"`), multiple fields (e.g. `"id name"`) or
nested selection sets (e.g. `"id user { name }"`).

Key directive should be specified on the root base type as well as all the corresponding federated (i.e. extended)
types. Key fields specified in the directive field set should correspond to a valid field on the underlying GraphQL
interface/object. Federated extended types should also instrument all the referenced key fields with `@external`
directive.

&gt; NOTE: The Federation spec specifies that multiple @key directives can be applied on the field. The GraphQL spec has been recently changed to allow this behavior,
&gt; but we are currently blocked and are tracking progress in [this issue](https://github.com/ExpediaGroup/graphql-kotlin/issues/590).

Example

```kotlin

@KeyDirective(FieldSet("id"))
class Product(val id: String, val name: String)

```

will generate

```graphql

type Product @key(fields: "id") {
  id: String!
  name: String!
}

```

## `@provides` directive

```graphql

directive @provides(fields: _FieldSet!) on FIELD_DEFINITION

```

The `@provides` directive is used to annotate the expected returned field set from a field on a base type that is
guaranteed to be selectable by the gateway. This allows you to expose only a subset of fields from the underlying
federated object type to be selectable from the federated schema. Provided fields specified in the directive field set
should correspond to a valid field on the underlying GraphQL interface/object type. `@provides` directive can only be
used on fields returning federated extended objects.

Example:
We might want to expose only name of the user that submitted a review.

```kotlin

@KeyDirective(FieldSet("id"))
class Review(val id: String) {
  @ProvidesDirective(FieldSet("name"))
  fun user(): User = // implementation goes here
}

@KeyDirective(FieldSet("userId"))
@ExtendsDirective
class User(
  @property:ExternalDirective val userId: String,
  @property:ExternalDirective val name: String
)

```

will generate

```graphql

type Review @key(fields : "id") {
  id: String!
  user: User! @provides(fields : "name")
}

type User @extends @key(fields : "userId") {
  userId: String! @external
  name: String! @external
}

```

## `@requires` directive

```graphql

directive @requires(fields: _FieldSet!) on FIELD_DEFINITON

```

The `@requires` directive is used to annotate the required input field set from a base type for a resolver. It is used
to develop a query plan where the required fields may not be needed by the client, but the service may need additional
information from other services. Required fields specified in the directive field set should correspond to a valid field
on the underlying GraphQL interface/object and should be instrumented with `@external` directive. Since `@requires`
directive specifies additional fields (besides the one specified in `@key` directive) that are required to resolve
federated type fields, this directive can only be specified on federated extended objects fields.

NOTE: fields specified in the `@requires` directive will only be specified in the queries that reference those fields.
This is problematic for Kotlin as the non nullable primitive properties have to be initialized when they are declared.
Simplest workaround for this problem is to initialize the underlying property to some dummy value that will be used if
it is not specified. This approach might become problematic though as it might be impossible to determine whether fields
was initialized with the default value or the invalid/default value was provided by the federated query. Another
potential workaround is to rely on delegation to initialize the property after the object gets created. This will ensure
that exception will be thrown if queries attempt to resolve fields that reference the uninitialized property.

Example:

```kotlin

@KeyDirective(FieldSet("id"))
@ExtendsDirective
class Product(@property:ExternalDirective val id: String) {
  @ExternalDirective
  var weight: Double by Delegates.notNull()

  @RequiresDirective(FieldSet("weight"))
  fun shippingCost(): String { ... }

  fun additionalInfo(): String { ... }
}

```

will generate

```graphql

type Product @extends @key(fields : "id") {
  additionalInfo: String!
  id: String! @external
  shippingCost: String! @requires(fields : "weight")
  weight: Float! @external
}

```
