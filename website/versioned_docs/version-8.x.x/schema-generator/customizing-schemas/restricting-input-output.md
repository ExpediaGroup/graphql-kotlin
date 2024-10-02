---
id: restricting-input-output
title: Restricting Input and Output Types
---

Since we are using Kotlin classes to represent both GraphQL input and output objects we can use the same class for both and the generator will handle type conflicts.

If you want to enforce that a type should never be used as an input or output you can use the `@GraphQLValidObjectLocations` annotation.
If the class was used in the schema in an invalid location an exception will be thrown.

```kotlin
class SimpleClass(val value: String)

@GraphQLValidObjectLocations([Locations.INPUT_OBJECT])
class InputOnly(val value: String)

@GraphQLValidObjectLocations([Locations.OBJECT])
class OutputOnly(val value: String)

// Valid Usage
fun output1() = SimpleClass("foo")
fun output2() = OutputOnly("foo")
fun input1(input: SimpleClass) = "value was ${input.value}"
fun input2(input: InputOnly) = "value was ${input.value}"

// Throws Exception
fun output3() = InputOnly("foo")
fun input3(input: OutputOnly) = "value was ${input.value}"
```
