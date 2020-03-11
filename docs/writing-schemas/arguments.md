---
id: arguments
title: Arguments
---

Method arguments are automatically exposed as part of the arguments to the corresponding GraphQL fields.

```kotlin
class SimpleQuery{

  @GraphQLDescription("performs some operation")
  fun doSomething(@GraphQLDescription("super important value") value: Int): Boolean = true
}
```

The above Kotlin code will generate following GraphQL schema:

```graphql
type Query {
  """performs some operation"""
  doSomething(
    """super important value"""
    value: Int!
  ): Boolean!
}
```

This behavior is true for all arguments except for the special classes for the [GraphQLContext](../execution/contextual-data) and the [DataFetchingEnvironment](../execution/data-fetching-environment)

### Input Types

Query and mutation function arguments are automatically converted to corresponding GraphQL input fields. GraphQL makes a
distinction between input and output types and requires unique names for all the types. Since we can use the same
objects for input and output in our Kotlin functions, `graphql-kotlin-schema-generator` will automatically append
`Input` suffix to the query input objects.

```kotlin
class WidgetMutation {

    @GraphQLDescription("modifies passed in widget so it doesn't have null value")
    fun processWidget(@GraphQLDescription("widget to be modified") widget: Widget): Widget {
        if (null == widget.value) {
            widget.value = 42
        }
        return widget
    }
}

@GraphQLDescription("A useful widget")
data class Widget(
    @GraphQLDescription("The widget's value that can be null")
    var value: Int? = nul
) {
    @GraphQLDescription("returns original value multiplied by target OR null if original value was null")
    fun multiplyValueBy(multiplier: Int) = value?.times(multiplier)
}
```

Will generate

```graphql
type Mutation {
  """modifies passed in widget so it doesn't have null value"""
  processWidget(
    """widget to be modified"""
    widget: WidgetInput!
  ): Widget!
}

"""A useful widget"""
type Widget {

  """The widget's value that can be null"""
  value: Int

  """
  returns original value multiplied by target OR null if original value was null
  """
  multiplyValueBy(multiplier: Int!): Int
}

"""A useful widget"""
input WidgetInput {

  """The widget's value that can be null"""
  value: Int
}

```

Please note that only fields are exposed in the input objects. Functions will only be available on the GraphQL output
types.

If you know a type will only be used for input types you can call your class `CustomTypeInput`. The library will not
append `Input` if the class name already ends with `Input` but that means you can not use this type as output because
the schema would have two types with the same name and will be invalid.

### Optional input fields

Kotlin requires variables/values to be initialized upon their declaration either from the user input OR by providing
defaults (even if they are marked as nullable). Therefore in order for GraphQL input field to be optional it needs to be
nullable and also specify default Kotlin value.

```kotlin
    @GraphQLDescription("query with optional input")
    fun doSomethingWithOptionalInput(
            @GraphQLDescription("this field is required") requiredValue: Int,
            @GraphQLDescription("this field is optional") optionalValue: Int?)
            = "required value=$requiredValue, optional value=$optionalValue"
```

NOTE: Non nullable input fields will always require users to specify the value regardless whether default Kotlin value
is provided or not.

NOTE: Even though you could specify a default value in Kotlin `optionalValue: Int? = null`, this will not be used since
if no value is provided to the schema `graphql-java` passes null as the value so the Kotlin default value will never be
used, like in this argument `optionalList: List<Int>? = emptyList()`, the value will be null if not passed a value by
the client.

### Default values

Default argument values are currently not supported. See issue
[#53](https://github.com/ExpediaGroup/graphql-kotlin/issues/53) for more details.
