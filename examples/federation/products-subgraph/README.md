# GraphQL Kotlin Federation Example - Products Subgraph

Apollo Federation example subgraph implementation using GraphQL Kotlin and exposing the federated `Product` type.

```graphql
type Query {
    product(id: ID!): Product
    products: [Product!]!
}

type Product @key(fields: "id") {
    id: ID!
    name: String!
    description: String
}
```

### Running locally

Build the application by running the following from examples root directory:

```bash
# build all examples
./gradlew build

# only build federation project
./gradlew :federation-products-subgraph:build
```

> NOTE: in order to ensure you use the right version of Gradle we highly recommend to use the provided wrapper scripts

Start the server:

* Run `ProductsApplication.kt` directly from your IDE
* Alternatively you can also use the Gradle Spring Boot plugin

```shell script
./gradlew :federation-products-subgraph:bootRun
```

Once the app has started you can explore the example schema by opening Playground endpoint at http://localhost:8080/playground.
