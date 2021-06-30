---
id: arguments
title: Arguments
---
Method arguments are automatically exposed as part of the arguments to the corresponding GraphQL fields.

```kotlin
class Query {
    fun doSomething(value: Int): Boolean = true
}
```

The above Kotlin code will generate following GraphQL schema:

```graphql
type Query {
  doSomething(value: Int!): Boolean!
}
```

This behavior is true for all arguments except for the special classes for the [GraphQLContext](../execution/contextual-data) and the [DataFetchingEnvironment](../execution/data-fetching-environment)

## Input Types

Query, Mutation, and Subscription function arguments are automatically converted to GraphQL input fields. GraphQL makes a
distinction between input and output types and requires unique names for all the types. Since we can use the same
objects for input and output in our Kotlin functions, `graphql-kotlin-schema-generator` will automatically append
an `Input` suffix to the GraphQL name of input objects.

For example, the following code:

```kotlin
class WidgetMutation {
    fun processWidget(widget: Widget): Widget {
        if (widget.value == null) {
            widget.value = 42
        }
        return widget
    }
}

data class Widget(var value: Int? = null) {
    fun multiplyValueBy(multiplier: Int): Int? = value?.times(multiplier)
}
```

Will generate the following schema:

```graphql
type Mutation {
  processWidget(widget: WidgetInput!): Widget!
}

type Widget {
  value: Int
  multiplyValueBy(multiplier: Int!): Int
}

input WidgetInput {
  value: Int
}
```

Note that only fields are exposed in the input objects. Functions will only be available on the GraphQL output types.

If you know a type will only be used for input types you can call your class something like `CustomTypeInput`. The library will not
append `Input` if the class name already ends with `Input` but that means you can not use this type as output because
the schema would have two types with the same name and that would be invalid.

If you would like to restrict an Kotlin class to only being used as input or output, see how to use [GraphQLValidObjectLocations](../customizing-schemas/restricting-input-output.md)

## Optional fields

Kotlin requires variables/values to be initialized upon their declaration either from the user input OR by providing
defaults (even if they are marked as nullable).

Therefore, in order for a GraphQL input field to be optional, **it needs to be nullable and must have a default value**.

```kotlin
fun doSomethingWithOptionalInput(requiredValue: Int, optionalValue: Int? = null): String {
    return "requiredValue=$requiredValue, optionalValue=$optionalValue"
}
```

## Default values

Default Kotlin values are supported, however the default value information is not available to the schema due to the [reflection limitations of Kotlin](https://github.com/ExpediaGroup/graphql-kotlin/issues/53).
The parameters must also be defined as optional (nullable) in the schema, as the only way a default value will be used is when the client does not specify any value in the request.

```kotlin
fun print(message: String? = "hello"): String? = message
```

The following operations will return the message in the comments

```graphql
query PrintMessages {
    first: print(message = "foo") # foo
    second: print(message = null) # null
    third: print # hello
}
```

If you need logic to determine when a client passed in a value vs when the default value was used (aka the argument was missing in the request), see [optional undefined arguments](../execution/optional-undefined-arguments.md).
