---
id: ktor-schema
title: Writing Schemas with Ktor
---

GraphQL schema, queries and mutation objects have to implement the corresponding marker interface. You can then configure
GraphQL plugin with references to your objects.

```kotlin
@ContactDirective(
    name = "My Team Name",
    url = "https://myteam.slack.com/archives/teams-chat-room-url",
    description = "send urgent issues to [#oncall](https://yourteam.slack.com/archives/oncall)."
)
@GraphQLDescription("My schema description")
class MySchema : Schema


class HelloWorldQuery : Query {
    fun hello(): String = "Hello World!"
}

class UpdateGreetingMutation : Mutation {
    fun updateGreeting(greeting: String): String = TODO()
}

fun Application.graphQLModule() {
    install(GraphQL) {
        schema {
            packages = listOf("com.example")
            queries = listOf(
                HelloWorldQuery()
            )
            mutations = listOf(
                UpdateGreetingMutation()
            )
            schemaObject = MySchema()
        }
    }
}
```

Above code will generate following GraphQL schema

```graphql
schema @contact(description : "send urgent issues to [#oncall](https://yourteam.slack.com/archives/oncall).", name : "My Team Name", url : "https://myteam.slack.com/archives/teams-chat-room-url"){
  query: Query
  mutation: Mutation
}

type Query {
  hello: String!
}

type Mutation {
    updateGreeting(greeting: String!): String!
}
```
