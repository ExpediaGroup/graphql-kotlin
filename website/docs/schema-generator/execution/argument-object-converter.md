---
id: argument-object-converter
title: Argument Object Converter
---
`ArgumentObjectConverter` allows application and library developers to provide their own implementation
and logic for how to convert the input argument. The `ArgumentObjectConverter` provide a way to extend
this library easily. It is using the service provider spec from `java.util.ServiceLoader`.
By default, `graphql-kotlin-schema-generator` provides `DefaultArgumentObjectConverter` to convert the input argument
value to the corresponding JVM object.

## Custom `ArgumentObjectConverter`

In order to use your custom implementations of the interface `ArgumentObjectConverter` you need to register your
custom implementations in the service file at
`META-INF/services/com.expediagroup.graphql.generator.execution.spi.ArgumentObjectConverter`
in your jar file with the fully qualified name of the class. You can put multiple implementation of the interface in
the service file.

### Example

Assume you want to provide a custom input object converter for class `Foo`.
You implement the interface `ArgumentObjectConverter` like `FooArgumentObjectConverter`:

```kotlin
package com.example.graphql.converters

data class Foo(val bar: String)

class FooArgumentObjectConverter : ArgumentObjectConverter {
    override val priority: Int = -1

    override fun <T : Any> doesSupport(targetClass: KClass<T>): Boolean = targetClass
            .isSubclassOf(Foo::class)

    override fun <T : Any> convert(input: Map<String, *>, targetClass: KClass<T>): T {
        return Foo(bar = input["baz"])
    }
}
```

You register your class `ArgumentObjectConverter` in the service provider file at
`META-INF/services/com.expediagroup.graphql.generator.execution.spi.ArgumentObjectConverter` with the content:

```text
# Line comment
com.example.graphql.converters.FooArgumentObjectConverter
```
