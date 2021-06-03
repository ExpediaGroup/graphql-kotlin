---
id: fetching-data
title: Fetching Data
original_id: fetching-data
---
Each field exposed through a GraphQL query has a corresponding resolver (aka data fetcher) associated with it. `graphql-kotlin-schema-generator` generates GraphQL schema
directly from the source code automatically mapping all the fields either to use
[FunctionDataFetcher](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/execution/FunctionDataFetcher.kt)
to resolve underlying functions or a [PropertyDataFetcher](https://www.graphql-java.com/documentation/v14/data-fetching/) to read a value from an underlying Kotlin property.

While all the fields in a GraphQL query are resolved independently to produce a final result, whether field is backed by a function or a property can have significant
performance repercussions. For example, given the following schema:

```graphql

type Query {
  product(id: Int!): Product
}

type Product {
  id: Int!
  name: String!
  reviews: [Review!]!
}

type Review {
  id: Int!
  text: String!
}

```

We can define `Product` as

```kotlin

data class Product(val id: Int, val name: String, reviews: List<Review>)

```

or

```kotlin

class Product(val id: Int, val name: String) {
  suspend fun reviews(): List<Reviews> {
     // logic to fetch reviews here
  }
}

```

If we expose the `reviews` field as a property it will always be populated regardless whether or not your client actually asks for it. On the other hand if `reviews` is backed
by a function, it will only be called if your client asks for this data. In order to minimize the over-fetching of data from your underlying data sources we recommend to
expose all your GraphQL fields that require some additional computations through functions.

### Customizing Default Behavior

You can provide your own custom data fetchers to resolve functions and properties by providing an instance of
[KotlinDataFetcherFactoryProvider](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/execution/KotlinDataFetcherFactoryProvider.kt#L31)
to your [SchemaGeneratorConfig](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-schema-generator/src/main/kotlin/com/expediagroup/graphql/SchemaGeneratorConfig.kt).
See our [spring example app](https://github.com/ExpediaGroup/graphql-kotlin/tree/3.x.x/examples/spring) for an example of `CustomDataFetcherFactoryProvider`.
