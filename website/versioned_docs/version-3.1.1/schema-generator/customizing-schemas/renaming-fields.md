---
id: version-3.1.1-renaming-fields
title: Renaming Fields
original_id: renaming-fields
---

By default, the schema generator will use the simple name of the underlying class for the type names and function/property names for fields.
You can change this default behavior by annotating the target class/field with `@GraphQLName`. The following Kotlin `Widget` class
will be renamed to `MyCustomName` GraphQL type and its fields will also be renamed.

```kotlin
@GraphQLName("MyCustomName")
data class Widget(
    @GraphQLName("myCustomField")
    val value: Int?
)
```

```graphql
type MyCustomName {
  myCustomField: Int
}
```

## Known Issues
> NOTE: Due to how we deserialize input classes, if you rename a field of an input class you must also annotate the field with the Jackson annotation @JsonProperty. See [issue 493](https://github.com/ExpediaGroup/graphql-kotlin/issues/493) for more info.

```kotlin
data class MyInputClass(
    @JsonProperty("renamedField")
    @GraphQLName("renamedField")
    val field1: String
)

class QueryClass {
  fun parseData(arg: MyInputClass) = "You sent ${arg.field1}"
}
```

```graphql
input MyInputClassInput {
  # This only works if both @JsonProperty and @GraphQLName are present
  renamedField: String!
}

type Query {
  parseData(arg: MyInputClass!): String!
}
```
