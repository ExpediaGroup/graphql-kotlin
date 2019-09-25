---
id: federated-schemas 
title: Federated Schemas
---
In many cases, exposing single GraphQL API that exposes unified view of all the available data provides tremendous value
to their clients. As the underlying graph scales, managing single monolithic GraphQL server might become less and less
feasible making it much harder to manage and leading to unnecessary bottlenecks. Migrating towards federated model with
an API gateway and a number of smaller GraphQL services behind it alleviates some of those problems and allows teams to
scale their graphs more easily.

## Apollo Federation

[Apollo Federation](https://www.apollographql.com/docs/apollo-server/federation/introduction/) is an architecture for
composing multiple GraphQL services into a single graph. Each individual GraphQL server generates valid GraphQL schema
and can be developed and run independently.

`graphql-kotlin-federation` extends the functionality of `graphql-kotlin-schema-generator` and allows you to easily
generate federated GraphQL schemas directly from the code. Federated schemas rely on a number of directives to
instrument the behavior of the underlying graph, see corresponding wiki pages to learn more about new directives. Once
all the federated objects are annotated, you will also have to configure corresponding [FederatedTypeResolver]s that are
used to instantiate federated objects and finally generate the schema using `toFederatedSchema` function
([link](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/federation/toFederatedSchema.kt#L34)).

**In order to generate valid federated schemas, you will need to annotate both your base schema and the one extending
it**. Federated Gateway (e.g. Apollo) will then combine the individual graphs to form single federated graph.

> NOTE: If you are using custom `Query` type then all of you federated GraphQL services have to use the same type. It is
> not possible for federated services to have multiple definitions of `Query` type.

### Base Schema

Base schema defines GraphQL types that will be extended by schemas exposed by other GraphQL services. In the example
below, we define base `Product` type with `id` and `description` fields. `id` is the primary key that uniquely
identifies the `Product` type object and is specified in `@key` directive.

```kotlin
@KeyDirective(fields = FieldSet("id"))
data class Product(val id: Int, val description: String)

class ProductQuery {
  fun product(id: Int): Product? {
    // grabs product from a data source, might return null
  }
}

// Generate the schema
val federatedTypeRegistry = FederatedTypeRegistry(emptyMap())
val config = FederatedSchemaGeneratorConfig(supportedPackages = listOf("org.example"), hooks = FederatedSchemaGeneratorHooks(federatedTypeRegistry))
val queries = listOf(TopLevelObject(ProductQuery()))

toFederatedSchema(config, queries)
```

Generates the following schema with additional federated types

```graphql
schema {
  query: Query
}

union _Entity = Product

type Product @key(fields : "id") {
  description: Int!
  id: String!
}

type Query @extends {
  _entities(representations: [_Any!]!): [_Entity]!
  _service: _Service
  product(id: Int!): Product!
}

type _Service {
  sdl: String!
}
```

#### Extended Schema

Extended federated GraphQL schemas provide additional functionality to the types already exposed by other GraphQL
services. In the example below, `Product` type is extended to add new `reviews` field to it. Primary key needed to
instantiate the `Product` type (i.e. `id`) has to match the `@key` definition on the base type. Since primary keys are
defined on the base type and are only referenced from the extended type, all of the fields that are part of the field
set specified in `@key` directive have to be marked as `@external`.

```kotlin
@KeyDirective(fields = FieldSet("id"))
@ExtendsDirective
data class Product(@ExternalDirective val id: Int) {

    fun reviews(): List<Review> {
        // returns list of product reviews
    }
}

data class Review(val reviewId: String, val text: String)

// Generate the schema
val productResolver = object: FederatedTypeResolver<Product> {
    override fun resolve(keys: Map<String, Any>): Product {
        val id = keys["id"]?.toString()?.toIntOrNull()
        // instantiate product using id
    }
}
val federatedTypeRegistry = FederatedTypeRegistry(mapOf("Product" to productResolver))
val config = FederatedSchemaGeneratorConfig(supportedPackages = listOf("org.example"), hooks = FederatedSchemaGeneratorHooks(federatedTypeRegistry))

toFederatedSchema(config)
```

Generates the following federated schema

```graphql
schema {
  query: Query
}

union _Entity = Product

type Product @extends @key(fields : "id") {
  id: Int! @external
  reviews: [Review!]!
}

type Query @extends {
  _entities(representations: [_Any!]!): [_Entity]!
  _service: _Service
}

type Review {
  reviewId: String!
  text: String!
}

type _Service {
  sdl: String!
}
```

Federated Gateway will then combine the schemas from the individual services to generate single schema.

#### Federated GraphQL schema

```graphql
schema {
  query: Query
}

type Product {
  description: String!
  id: String!
  reviews: [Review!]!
}

type Review {
  reviewId: String!
  text: String!
}

type Query {
  product(id: String!): Product!
}
```
