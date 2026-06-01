---
id: optional-undefined-arguments
title: Optional Undefined Arguments
---
In the GraphQL world, input types can be optional which means that the client can either:

-   Not specify a value at all
-   Send null explicitly
-   Send a non-null value

This is in contrast with the JVM world where objects can either have some specific value or don't have any value (i.e.
are `null`). As a result, when using default serialization logic it is not possible to distinguish between missing/unspecified
value and explicit `null` value.

## Using OptionalInput wrapper

`OptionalInput` is a convenient sealed class wrapper that provides distinction between undefined, null, and non-null
values. If the argument is not specified in the request it will be represented as a `OptionalInput.Undefined` object, otherwise the
value will be wrapped in `OptionalInput.Defined` class. As a best practice, we highly recommend to set appropriate
[default values to all optional arguments](https://opensource.expediagroup.com/graphql-kotlin/docs/schema-generator/writing-schemas/arguments#default-values).

```kotlin
fun optionalInput(input: OptionalInput<String> = OptionalInput.Undefined): String = when (input) {
    is OptionalInput.Undefined -> "input was not specified"
    is OptionalInput.Defined<String> -> "input value: ${input.value}"
}
```

```graphql
query OptionalInputQuery {
  undefined: optionalInput # input was not specified
  null: optionalInput(value: null) # input value: null
  foo: optionalInput(value: "foo") # input value: foo
}
```

:::info
Regardless whether the generic type of `OptionalInput` is specified as nullable or not it will always result in a nullable
value in `Defined` class, i.e. `OptionalInput<String>` will appear as nullable `String` in the GraphQL schema and in the wrapped value.
:::

## Using DataFetchingEnvironment

Optional input types can be represented as nullable parameters in Kotlin

```kotlin
fun optionalInput(value: String? = null): String? = value
```

```graphql
query OptionalInputQuery {
  undefined: optionalInput # null
  null: optionalInput(value: null) # null
  foo: optionalInput(value: "foo") # foo
}
```

By default, if an optional input value is not specified, then the execution engine will fallback to the argument default
value (in our example above `null`). This means that you can not tell, by just the value alone, whether the request did
not contain any argument or the client explicitly passed in the default value.

Instead, you can inspect all passed in arguments using the [DataFetchingEnvironment](./data-fetching-environment.md).

```kotlin
fun optionalInput(value: String? = null, dataFetchingEnvironment: DataFetchingEnvironment): String =
    if (dataFetchingEnvironment.containsArgument("value")) {
        "The value was $value"
    } else {
        "The value was undefined"
    }
```

## Kotlin Default Values

If you don't need logic for when the client specified a value, you can still use [Kotlin default values](../writing-schemas/arguments.md)
