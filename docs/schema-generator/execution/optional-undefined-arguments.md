---
id: optional-undefined-arguments
title: Optional Undefined Arguments
---

In the GraphQL world, input types can be optional which means that the client can either:

* Not specify a value at all
* Send null explicitly
* Send the non-null type

This is in contrast with the JVM world where objects can either have some specific value or don't have any value (i.e.
are `null`). As a result, when using default serialization logic it is not possible to distinguish between missing/unspecified
value and explicit `null` value.

## Using OptionalInput wrapper

`OptionalInput` sealed class is a convenient wrapper that provides easy distinction between unspecified, `null` and non-null
value. If target argument is not specified in the request it will be represented as `Undefined` object, otherwise actual
value will be wrapped in `Defined` class.

```kotlin
fun optionalInput(input: OptionalInput<String>): String = when (input) {
    is OptionalInput.Undefined -> "input was not specified"
    is OptionalInput.Defined<String> -> "input value: ${input.value}"
}
```

```graphql
query OptionalInputQuery {
  undefined: optionalInput
  null: optionalInput(value: null)
  foo: optionalInput(value: "foo")
}
```

> NOTE: Regardless whether generic type of `OptionalInput` is specified as nullable or not it will always result in nullable
> value in `Defined` class.

## Using DataFetchingEnvironment

Optional input types can be represented as nullable parameters in Kotlin

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
This means that you can not tell, by just the value alone, whether the request did not contain any argument or the client
explicitly passed in `null`.

Instead, you can inspect all passed in arguments using the [DataFetchingEnvironment](./data-fetching-environment.md).

```kotlin
fun optionalInput(value: String?, dataFetchingEnvironment: DataFetchingEnvironment): String =
    if (dataFetchingEnvironment.containsArgument("value")) {
        "The value was $value"
    } else {
        "The value was undefined"
    }
```
