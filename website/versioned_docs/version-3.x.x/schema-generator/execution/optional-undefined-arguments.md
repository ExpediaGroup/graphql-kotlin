---
id: optional-undefined-arguments
title: Optional Undefined Arguments
original_id: optional-undefined-arguments
---
In GraphQL, input types can be optional which means that the client can either:

-   Not specify a value at all
-   Send null explictly
-   Send the non-null type

Optional input types are represented as nullable parameters in Kotlin

```kotlin

fun optionalInput(value: String?): String? = value

```

```graphql

query OptionalInputQuery {
  undefined: optionalInput
  null: optionalInput(value: null)
  foo: optionalInput(value: "foo")
}

```

By default, if an optional input value is not specified, then the execution engine will set the argument in Kotlin to `null`.
This means that you can not tell, by just the value alone, whether the request did not contain any argument or the client explicitly passed in `null`.

Instead, you should inspect the [DataFetchingEnvironment](./data-fetching-environment.md) where you can see if the request had the variable defined and even check parent arguments as well.

```kotlin

fun optionalInput(value: String?, dataFetchingEnvironment: DataFetchingEnvironment): String =
    if (dataFetchingEnvironment.containsArgument("value")) {
        "The value was $value"
    } else {
        "The value was undefined"
    }

```
