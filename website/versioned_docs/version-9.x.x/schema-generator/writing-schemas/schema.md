---
id: schema
title: Schema
---
## Schema Object

`SchemaGenerator` automatically generates schema object from the provided list of `TopLevelObjects` representing `queries`, `mutations` and `subscriptions`.

In order to provide [schema description](../customizing-schemas/documenting-schema.md) or to specify [schema directives](../customizing-schemas/directives.md), we need to provide `TopLevelObject` reference to a schema class.

:::caution
Only annotations are processed on the schema object. Generator will throw an exception if schema class contains any properties or functions.
:::

In the example below, we provide custom description and apply `@contact` directive on the schema object.

```kotlin
@ContactDirective(
  name = "My Team Name",
  url = "https://myteam.slack.com/archives/teams-chat-room-url",
  description = "send urgent issues to [#oncall](https://yourteam.slack.com/archives/oncall)."
)
@GraphQLDescription("My schema description")
class MySchema

class HelloWorldQuery {
    fun helloWorld() = "Hello World!"
}

// generate schema
val schema = toSchema(
    config = yourCustomConfig(),
    queries = listOf(TopLevelObject(HelloWorldQuery())),
    schemaObject = TopLevelObject(MySchema())
)
```

Will generate

```graphql
schema @contact(description : "send urgent issues to [#oncall](https://yourteam.slack.com/archives/oncall).", name : "My Team Name", url : "https://myteam.slack.com/archives/teams-chat-room-url"){
  query: Query
}

type Query {
    helloWorld: String!
}
```

:::note
`graphql-java` currently does not include schema description in the generated SDL (it is available in the introspection results only).
Fixed in https://github.com/graphql-java/graphql-java/pull/2856.
:::
