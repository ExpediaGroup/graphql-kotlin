---
id: spring-schema
title: Writing Schemas with Spring
---

In order to expose your queries, mutations, and subscriptions in the GraphQL schema create beans that
implement the corresponding marker interface and they will be automatically picked up by `graphql-kotlin-spring-server`
auto-configuration library.

```kotlin
data class Widget(val id: ID, val value: String)

@Component
class WidgetQuery : Query {
  fun widget(id: ID): Widget = getWidgetFromDB(id)
}

@Component
class WidgetMutation : Mutation {
  fun updateWidget(id: ID, value: String): Boolean = updateWidgetInDB(id, value)
}

@Component
class WidgetSubscription : Subscription {
  fun widgetChanges(id: ID): Publisher<Widget> = getPublisherOfUpdates(id)
}
```

will result in a Spring Boot reactive GraphQL web application with following schema.

```graphql
schema {
  query: Query
  mutation: Mutation
  subscription: Subscription
}

type Widget {
    id: ID!
    value: String!
}

type Query {
  widget(id: ID!): Widget!
}

type Mutation {
    updateWidget(id: ID!, value: String!): Boolean!
}

type Subscription {
    widgetChanges(id: ID!): Widget!
}
```

## Spring Beans

Since the top level objects are Spring components, Spring will automatically autowire dependent beans as normal. Refer to [Spring Documentation](https://docs.spring.io/spring/docs/current/spring-framework-reference/) for details.

```kotlin
@Component
class WidgetQuery(private val repository: WidgetRepository) : Query {
    fun getWidget(id: Int): Widget = repository.findWidget(id)
}
```

## Spring Beans in Arguments

`graphql-kotlin-spring-server` provides Spring-aware data fetcher that automatically autowires Spring beans when they are
specified as function arguments. `@Autowired` arguments should be explicitly excluded from the GraphQL schema by also
specifying `@GraphQLIgnore`.

```kotlin
@Component
class SpringQuery : Query {
    fun getWidget(@GraphQLIgnore @Autowired repository: WidgetRepository, id: Int): Widget = repository.findWidget(id)
}
```

:::note
If you are using custom data fetcher make sure that you extend `SpringDataFetcher` instead of the base `FunctionDataFetcher` to keep this functionallity.
:::

We have examples of these techniques implemented in Spring boot in the [example
app](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/examples/server/spring-server/src/main/kotlin/com/expediagroup/graphql/examples/server/spring/query/NestedQueries.kt).
