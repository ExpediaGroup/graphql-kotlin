# GraphQL Kotlin Federated Schema Generator

Extension to `graphql-kotlin-schema-generator` that can be used to generate federated GraphQL schemas.

See more
* [Federation Spec](https://www.apollographql.com/docs/apollo-server/federation/federation-spec/)

## Installation

Using a JVM dependency manager, simply link `graphql-kotlin-federation` to your project.

With Maven:

```xml
<dependency>
  <groupId>com.expedia</groupId>
  <artifactId>graphql-kotlin-federation</artifactId>
  <version>${latestVersion}</version>
</dependency>
```

With Gradle:

```groovy
compile(group: 'com.expedia', name: 'graphql-kotlin-federation', version: "$latestVersion")
```

## Usage

In order to generate valid federated schemas, you will need to annotate your both base service and the one extending it. Federated Gateway (e.g. Apollo) will then combine the individual graphs to form single federated graph.

> Base Service

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

#Space separated list of primary keys needed to access federated object
directive @key(fields: _FieldSet!) on OBJECT | INTERFACE

union _Entity = Product

type Product @key(fields : "id") {
  description: String!
  id: String!
}

type Query {
  #Union of all types that use the @key directive, including both types native to the schema and extended types
  _entities(representations: [_Any!]!): [_Entity]!
  _service: _Service
  product(id: String!): Product!
}

type _Service {
  sdl: String!
}

#Federation scalar type used to represent any external entities passed to _entities query.
scalar _Any

#Federation type representing set of fields
scalar _FieldSet
```

> Extended Service

```kotlin
@KeyDirective(fields = FieldSet("id"))
@ExtendsDirective
data class Product(@property:ExternalDirective val id: Int) {

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

#Marks target field as external meaning it will be resolved by federated schema
directive @external on FIELD_DEFINITION

#Marks target object as part of the federated schema
directive @extends on OBJECT | INTERFACE

#Space separated list of primary keys needed to access federated object
directive @key(fields: _FieldSet!) on OBJECT | INTERFACE

union _Entity = Product

type Product @extends @key(fields : "id") {
  id: Int! @external
  reviews: [Review!]!
}

type Query {
  #Union of all types that use the @key directive, including both types native to the schema and extended types
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

#Federation scalar type used to represent any external entities passed to _entities query.
scalar _Any

#Federation type representing set of fields
scalar _FieldSet
```

Federated Gateway will then combine the schemas from the individual services to generate single schema.

## Documentation

There are more examples and documentation in our [Wiki](https://github.com/ExpediaDotCom/graphql-kotlin/wiki) or you can view the [javadocs](https://www.javadoc.io/doc/com.expedia/graphql-kotlin) for all published versions.

If you have a question about something you can not find in our wiki or javadocs, feel free to [create an issue](https://github.com/ExpediaDotCom/graphql-kotlin/issues) and tag it with the question label.
