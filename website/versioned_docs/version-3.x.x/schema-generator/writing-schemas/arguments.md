---
id: arguments
title: Arguments
original_id: arguments
---
Method arguments are automatically exposed as part of the arguments to the corresponding GraphQL fields.

```kotlin

class SimpleQuery{
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

Query and mutation function arguments are automatically converted to corresponding GraphQL input fields. GraphQL makes a
distinction between input and output types and requires unique names for all the types. Since we can use the same
objects for input and output in our Kotlin functions, `graphql-kotlin-schema-generator` will automatically append
an `Input` suffix to the query input objects.

For example, the following code:

```kotlin

class WidgetMutation {
    fun processWidget(widget: Widget): Widget {
        if (null == widget.value) {
            widget.value = 42
        }
        return widget
    }
}

data class Widget(var value: Int? = nul) {
    fun multiplyValueBy(multiplier: Int) = value?.times(multiplier)
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

Please note that only fields are exposed in the input objects. Functions will only be available on the GraphQL output
types.

If you know a type will only be used for input types you can call your class something like `CustomTypeInput`. The library will not
append `Input` if the class name already ends with `Input` but that means you can not use this type as output because
the schema would have two types with the same name and that would be invalid.

## Optional input fields

Kotlin requires variables/values to be initialized upon their declaration either from the user input OR by providing
defaults (even if they are marked as nullable). Therefore in order for a GraphQL input field to be optional it needs to be
nullable and also specify a default Kotlin value.

```kotlin

fun doSomethingWithOptionalInput(requiredValue: Int, optionalValue: Int?) = "required value=$requiredValue, optional value=$optionalValue"

```

NOTE: Non nullable input fields will always require users to specify the value regardless of whether a default Kotlin value
is provided or not.

NOTE: Even though you could specify a default values for arguments in Kotlin `optionalValue: Int? = null`, this will not
be used. If query does not explicitly specify root argument values, our function data fetcher will default to use null as
the value. This is because Kotlin properties always have to be initialized, and we cannot determine whether underlying
argument has default value or not. As a result, Kotlin default value will never be used. For example, with argument
`optionalList: List<Int>? = emptyList()`, the value will be null if not passed a value by the client.

See [optional undefined arguments](../execution/optional-undefined-arguments) for details how to determine whether argument
was specified or not.

## Default values

Default argument values are currently not supported. See issue [#53](https://github.com/ExpediaGroup/graphql-kotlin/issues/53)
for more details.
