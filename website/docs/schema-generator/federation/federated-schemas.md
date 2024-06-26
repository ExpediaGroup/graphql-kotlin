---
id: federated-schemas
title: Federated Schemas
---
`graphql-kotlin-federation` library extends the functionality of the `graphql-kotlin-schema-generator` and allows you to
easily generate federated GraphQL schemas directly from the code. Federated schema is generated by calling
`toFederatedSchema` function that accepts federated configuration as well as a list of regular queries, mutations and
subscriptions exposed by the schema.

All [federated directives](federated-directives) are provided as annotations that are used to decorate your classes,
properties and functions. Since federated types might not be accessible through the regular query execution path, they
are explicitly picked up by the schema generator based on their directives. Due to the above, we also need to provide
a way to instantiate the underlying federated objects by implementing corresponding `FederatedTypeResolvers`. See
[type resolution wiki](type-resolution) for more details on how federated types are resolved. Final federated schema
is then generated by invoking the `toFederatedSchema` function
([link](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/generator/federation/toFederatedSchema.kt#L34)).

**In order to generate valid federated schemas, you will need to annotate your entities in all your subgraphs**.
Federated Gateway (e.g. Apollo) will then combine the individual graphs to form single federated graph.

:::caution
If you are using custom `Query` type then all of you federated GraphQL services have to use the same type. It is
not possible for federated services to have different definitions of `Query` type.
:::

### Subgraph A

Federation v2 relaxed entity ownership and now every subgraph that defines given entity is its owner. In the example
below, we define `Product` type with `id` and `description` fields. `id` is the primary key that uniquely
identifies the `Product` type object and is specified in `@key` directive. Since it might be possible to resolve
`Product` entity from other subgraphs, we also should specify an "entry point" for the federated type - we need to
create a `FederatedTypeResolver` that will be used to instantiate the federated `Product` type when processing federated
queries.

```kotlin
@KeyDirective(fields = FieldSet("id"))
data class Product(val id: Int, val description: String)

class ProductQuery {
  fun product(id: Int): Product? {
    // grabs product from a data source, might return null
  }
}

// Resolve a "Product" type from the _entities query
class ProductResolver : FederatedTypeSuspendResolver<Product> {
    override val typeName = "Product"

    override suspend fun resolve(
        environment: DataFetchingEnvironment,
        representation: Map<String, Any>
    ): Product? =
        representation["id"]?.toString()?.toIntOrNull()?.let { id -> Product(id) }
}

// Generate the schema
val resolvers = listOf(ProductResolver())
val hooks = FederatedSchemaGeneratorHooks(resolvers)
val config = FederatedSchemaGeneratorConfig(supportedPackages = listOf("org.example"), hooks = hooks)
val queries = listOf(TopLevelObject(ProductQuery()))

toFederatedSchema(config, queries)
```

Example above generates the following schema with additional federated types:

```graphql
schema {
  query: Query
}

union _Entity = Product

type Product @key(fields : "id") {
  description: String!
  id: Int!
}

type Query {
  _entities(representations: [_Any!]!): [_Entity]!
  _service: _Service!
  product(id: Int!): Product
}

type _Service {
  sdl: String!
}
```

### Subgraph B

Each subgraph can extend and provide new functionality to entities defined in other subgraphs. In the example below,
`Product` type is extended to add new `reviews` field to it. Primary key needed to instantiate the `Product` type (i.e. `id`)
has to match one of the entity `@key` definitions defined in other subgraphs. Finally, we also need to specify an "entry point"
for the federated type - we need to create a FederatedTypeResolver.

```kotlin
@KeyDirective(fields = FieldSet("id"))
data class Product(val id: Int) {
    // Add the "reviews" field to the type
    suspend fun reviews(): List<Review> = getReviewByProductId(id)
}

data class Review(val reviewId: String, val text: String)

// Resolve a "Product" type from the _entities query
class ProductResolver : FederatedTypeSuspendResolver<Product> {
    override val typeName = "Product"

    override suspend fun resolve(
        environment: DataFetchingEnvironment,
        representation: Map<String, Any>
    ): Product? =
        representation["id"]?.toString()?.toIntOrNull()?.let { id -> Product(id) }
}

// Generate the schema
val resolvers = listOf(ProductResolver())
val hooks = FederatedSchemaGeneratorHooks(resolvers)
val config = FederatedSchemaGeneratorConfig(supportedPackages = listOf("org.example"), hooks = hooks)

toFederatedSchema(config)
```

Our extended schema will then be generated as:

```graphql
schema {
  query: Query
}

union _Entity = Product

type Product @key(fields : "id") {
  id: Int!
  reviews: [Review!]!
}

type Query {
  _entities(representations: [_Any!]!): [_Entity]!
  _service: _Service!
}

type Review {
  reviewId: String!
  text: String!
}

type _Service {
  sdl: String!
}
```

### Federated Supergraph

Once we have both GraphQL subgraphs up and running, we will also need to configure Federated Gateway
to combine them into a single supergraph schema. Using the examples above, our final federated schema will be generated as:

```graphql
schema {
  query: Query
}

type Product {
  description: String!
  id: Int!
  reviews: [Review!]!
}

type Review {
  reviewId: String!
  text: String!
}

type Query {
  product(id: String!): Product
}
```

See our [federation example](https://github.com/ExpediaGroup/graphql-kotlin/tree/master/examples/federation) for additional details.
