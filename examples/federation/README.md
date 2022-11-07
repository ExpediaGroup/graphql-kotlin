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








is two Spring applications `base-app` and `extend-app` that use `graphql-kotlin-federation` to generate the schema.
These apps run on different ports (`8080`, `8081`) so they can run simultaneously.

The `gateway` is a Node.js app running Apollo Gateway on port `4000` and connects to the two Spring apps.
You can make queries against the Spring apps directly or run combined queries from the gateway.

## Running Locally


### Spring Apps
Build the Spring applications by running the following commands in the `/federation` directory

```shell script
./gradlew clean build
```

> NOTE: in order to ensure you use the right version of Gradle we highly recommend to use the provided wrapper scripts

Start the servers:

* Run each `Application.kt` directly from your IDE
* Alternatively you can also use the spring boot plugin from the command line.

```shell script
./gradlew bootRun
```


Once the app has started you can explore the example schema by opening the Playground endpoint
* `base-app` http://localhost:8080/playground
* `extend-app` http://localhost:8081/playground

### Gateway

See the instructions in the gateway [README](./gateway/README.md)



1. Start `products-subgraph` by running the Spring Boot app from the IDE or by running `./gradlew bootRun` from `products-subgraph` project
2. Start `reviews-subgraph` by running the Spring Boot app from the IDE or `./gradlew bootRun` from `reviews-subgraph` project
3. Start Federated Router
    1. Install [rover CLI](https://www.apollographql.com/docs/rover/getting-started)
    2. Start router and compose products schema using [rover dev command](https://www.apollographql.com/docs/rover/commands/dev)

    ```shell
    # start up router and compose products schema
    rover dev --name products --schema ./products-subgraph/src/main/resources/graphql/schema.graphqls --url http://localhost:8080/graphql
    ```

    3. In **another** shell run `rover dev` to compose reviews schema

    ```shell
    rover dev --name reviews --schema ./reviews-subgraph/src/main/resources/graphql/schema.graphqls --url http://localhost:8080/graphql
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
