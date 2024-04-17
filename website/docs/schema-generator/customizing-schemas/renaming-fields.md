---
id: renaming-fields
title: Renaming Fields
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

:::info
By default, in order to differentiate between input and output types, all input type names are auto generated with additional
`Input` suffix. If you would like to change this behavior and avoid extra suffix, you need to explicitly specify that this
is an input only type.

```kotlin
// GraphQL input object type name: MyInputType
@GraphQLValidObjectLocations([GraphQLValidObjectLocations.Locations.INPUT_OBJECT])
data class MyInputType(val id: ID)
```

You can also rename input types

```kotlin
// GraphQL input object type name: MyCustomInputType
@GraphQLValidObjectLocations([GraphQLValidObjectLocations.Locations.INPUT_OBJECT])
@GraphQLName("MyCustomInputType")
data class MyInputType(val id: ID)
```
:::

## Known Issues

Due to how we deserialize input classes, if you rename a field of an input class or an enum value you must also annotate
it with the Jackson annotation `@JsonProperty`. See [issue 493](https://github.com/ExpediaGroup/graphql-kotlin/issues/493)
for more info.

```kotlin
data class MyInputClass(
    @JsonProperty("renamedField")
    @GraphQLName("renamedField")
    val field1: String
)

// GraphQL enums should use UPPER_CASE naming if possible, but any case is supported
enum class Selection {

  @JsonProperty("first")
  @GraphQLName("first")
  ONE,

  @JsonProperty("second")
  @GraphQLName("second")
  TWO
}

class QueryClass {
  fun parseData(arg: MyInputClass) = "You sent ${arg.field1}"

  fun chooseValue(selection: Selection): String = when (selection) {
    Selection.ONE -> "You chose the first value"
    Selection.TWO -> "You chose the second value"
  }
}
```

```graphql
input MyInputClassInput {
  # This only works if both @JsonProperty and @GraphQLName are present
  renamedField: String!
}

enum Selection {
  first,
  second
}

type Query {
  parseData(arg: MyInputClass!): String!
  chooseValue(selection: Selection!): String!
}
```
