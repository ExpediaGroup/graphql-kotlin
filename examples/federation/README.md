# Federation Example

[Apollo Federation](https://www.apollographql.com/docs/federation/) example using GraphQL Kotlin and [Apollo Router](https://www.apollographql.com/docs/router/).

The repository contains two separate projects:

1. `products-subgraph`: A Java GraphQL service providing the federated `Product` type
2. `reviews-subgraph`: A Java GraphQL service that extends the `Product` type with `reviews`

See individual projects READMEs for detailed instructions on how to run them.

## Running example locally

1. Start `products-subgraph` by running the Spring Boot app from the IDE or by running `gradle bootRun` from `products-subgraph` project
2. Start `reviews-subgraph` by running the Spring Boot app from the IDE or `gradle bootRun` from `reviews-subgraph` project
3. Start Federated Router
    1. Install [rover CLI](https://www.apollographql.com/docs/rover/getting-started)
    2. Start router and compose products schema using [rover dev command](https://www.apollographql.com/docs/rover/commands/dev)

    ```shell
    # start up router and compose supergraph schema, assuming
    rover dev --supergraph-config <path to supergraph.yaml>
    ```

4. Open http://localhost:3000 for the query editor

Example federated query

```graphql
query ExampleQuery {
    products {
        id
        name
        description
        reviews {
            id
            text
            starRating
        }
    }
}
```
