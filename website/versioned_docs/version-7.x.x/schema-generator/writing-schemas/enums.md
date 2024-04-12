---
id: enums
title: Enums
---
Enums are automatically mapped to GraphQL enum type.

```kotlin
enum class MyEnumType {
  ONE,
  TWO
}
```

Above enum will be generated as following GraphQL object

```graphql
enum MyEnumType {
  ONE
  TWO
}
```

### Converting a Java enum to a GraphQL Enum

If you want to use Java enums from another package, but you **don't** want
include everything from that package using [`supportedPackages`][config] or you want
to customize the GraphQL type, you can use [schema generator hooks][config] to
associate the Java enum with a runtime [`GraphQLEnumType`][javadoc].

[config]: ../customizing-schemas/generator-config.md

[javadoc]: https://javadoc.io/doc/com.graphql-java/graphql-java/latest/index.html

Step 1: Create a GraphQLEnumType using the Java enum values

```java
// in some other package
public enum Status {
  APPROVED,
  DECLINED
}
```

```kotlin
val statusEnumType = GraphQLEnumType.newEnum()
    .name("Status")
    .values(Status.values().map {
      GraphQLEnumValueDefinition.newEnumValueDefinition()
          .value(it.name)
          .build()
    })
    .build()
```

Step 2: Add a schema generation hook

```kotlin
class CustomSchemaGeneratorHooks : SchemaGeneratorHooks {

  override fun willGenerateGraphQLType(type: KType): GraphQLType? {
    return when (type.classifier as? KClass<*>) {
      Status::class.java -> statusEnumType
      else -> super.willGenerateGraphQLType(type)
    }
  }
}
```

Step 3. Use your Java enum anywhere in your schema

```kotlin
@Component
class StatusQuery : Query {
  fun currentStatus: Status = getCurrentStatus()
}
```
