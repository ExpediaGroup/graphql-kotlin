---
id: fields
title: Types and Fields
---
`graphql-kotlin-schema-generator` uses [reflection](https://kotlinlang.org/docs/reflection.html) to automatically map
Kotlin classes and enums to the corresponding GraphQL types.

By default, all public properties and functions with a [valid GraphQL name](https://spec.graphql.org/draft/#sec-Names)
and a supported return type will be mapped to a corresponding GraphQL field. Kotlin return types have to be either one of the
[supported scalars](./scalars.md) or a custom Kotlin type defined within supported packages. Nullability information is
automatically inferred from the underlying Kotlin return type.

Additional built-in validations
* function types aka lambdas property types are currently not supported by the schema generator
* automatically generated data class methods (`componentN`, `copy`, `equals`, `hashCode`, `toString`) are filtered out

Default behavior can be customized. Fields can be [renamed](../customizing-schemas/renaming-fields.md) or [excluded](../customizing-schemas/excluding-fields.md).
Support for additional types or validations can be added by providing an instance of custom [SchemaGeneratorHook](../customizing-schemas/generator-config.md#schemageneratorhooks).

## Type Inheritance

`graphql-kotlin-schema-generator` provides support for both GraphQL [unions](./unions.md) and [interfaces](./interfaces.md).
Superclasses and interfaces can be excluded from the schema by marking them with `@GraphQLIgnore` annotation or by providing
custom filtering logic in a custom [SchemaGeneratorHook](../customizing-schemas/generator-config.md#schemageneratorhooks).
