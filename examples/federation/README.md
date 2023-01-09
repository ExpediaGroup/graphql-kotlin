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
    # start up router and compose products schema
    rover dev --name products --url http://localhost:8080/graphql
    ```

    3. In **another** shell run `rover dev` to compose reviews schema

    ```shell
    rover dev --name reviews --url http://localhost:8081/graphql
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
