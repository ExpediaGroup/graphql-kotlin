# GraphQL Kotlin Federation Example - Reviews Subgraph

Apollo Federation example subgraph implementation using GraphQL Kotlin and exposing the federated `Product` type.

```graphql
type Product @key(fields: "id") {
    id: ID!
    reviews: [Review!]!
}

type Review {
    id: ID!,
    text: String
    starRating: Int!
}
```

### Running locally

Build the application by running the following from examples root directory:

```bash
# build all examples
./gradlew build

# only build federation project
./gradlew :federation-reviews-subgraph:build
```

> NOTE: in order to ensure you use the right version of Gradle we highly recommend to use the provided wrapper scripts

Start the server:

* Run `ReviewsApplication.kt` directly from your IDE
* Alternatively you can also use the Gradle Spring Boot plugin

```shell script
./gradlew :federation-reviews-subgraph:bootRun
```

Once the app has started you can explore the example schema by opening Playground endpoint at http://localhost:8081/playground.
