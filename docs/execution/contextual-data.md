---
id: contextual-data
title: Contextual Data
---

All GraphQL servers have a concept of a "context". A GraphQL context contains metadata that is useful to the GraphQL
server, but shouldn't necessarily be part of the GraphQL schema. A prime example of something that is appropriate
for the GraphQL context would be trace headers for an OpenTracing system such as
[Haystack](https://expediadotcom.github.io/haystack). The GraphQL query does not need the information to perform
its function, but the server needs the information to ensure observability.

The contents of the GraphQL context vary across applications and it is up to the GraphQL server developers to decide
what it should contain. For Spring based applications, `graphql-kotlin-spring-server` provides a simple mechanism to
build context per query execution through
[GraphQLContextFactory](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/GraphQLContextFactory.kt).
Once context factory bean is available in the Spring application context it will then be used in a corresponding
[ContextWebFilter](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-spring-server/src/main/kotlin/com/expediagroup/graphql/spring/execution/ContextWebFilter.kt)
to populate GraphQL context based on the incoming request and make it available during query execution. See [graphql-kotlin-spring-server documentation](../spring-server/spring-graphql-context)
for additional details

## GraphQLContext Interface

The easiest way to specify a context class is to use the `GraphQLContext` marker interface. This interface does not require any implementations,
it is just used to inform the schema generator that this is the class that should be used as the context for every request.

```kotlin
class MyGraphQLContext(val customValue: String) : GraphQLContext
```

Then you can just use the class as an argument and it will be automaticall injected during execution time.

```kotlin
class ContextualQuery {
    fun contextualQuery(
        context: MyGraphQLContext,
        value: Int
    ): String = "The custom value was ${context.customValue} and the value was $value"
}
```

The above query would produce the following GraphQL schema:

```graphql
schema {
  query: Query
}

type Query {
  contextualQuery(value: Int!): String!
}
```

Note that the argument that implements `GraphQLContext` is not reflected in the GraphQL schema.


### GraphQLContext Annotation

From the 1.0.0 release we also support marking any argument with the annotaiton `@GraphQLContext`.
If the schema generator sees this annotation on an argument, it will assume that this is the context class and inject the context as this argument value.
This does require that you mark **all** usages of the arument with the annotation. This will cause an error if you incorrectly mark the wrong argument,
which is why the interface method may be better, but the annotation can be helpful if you do no control the implementation of the context class you are using.

```kotlin
class MyGraphQLContext(val customValue: String)

class ContextualQuery {
    fun contextualQuery(
        @GraphQLContext context: MyGraphQLContext,
        value: Int
    ): String = "The custom value was ${context.customValue} and the value was $value"
}
```
