---
id: apollo-federation
title: Apollo Federation
original_id: apollo-federation
---
In many cases, exposing single GraphQL API that exposes unified view of all the available data provides tremendous value
to their clients. As the underlying graph scales, managing single monolithic GraphQL server might become less and less
feasible making it much harder to manage and leading to unnecessary bottlenecks. Migrating towards federated model with
an API gateway and a number of smaller GraphQL services behind it alleviates some of those problems and allows teams to
scale their graphs more easily.

[Apollo Federation](https://www.apollographql.com/docs/apollo-server/federation/introduction/) is an architecture for
composing multiple GraphQL services into a single graph. Federated schemas rely on a number of custom directives to
instrument the behavior of the underlying graph and convey the relationships between different schema types. Each individual
GraphQL server generates a valid GraphQL schema and can be run independently. This is in contrast with traditional schema
stitching approach where relationships between individual services, i.e. linking configuration, is configured at the GraphQL
Gateway level.

## Install

Using a JVM dependency manager, link `graphql-kotlin-federation` to your project.

With Maven:

```xml

<dependency>
  <groupId>com.expediagroup</groupId>
  <artifactId>graphql-kotlin-federation</artifactId>
  <version>${latestVersion}</version>
</dependency>

```

With Gradle:

```kotlin

implementation("com.expediagroup", "graphql-kotlin-federation", latestVersion)

```

## Usage

`graphql-kotlin-federation` build on top of `graphql-kotlin-schema-generator` and adds a few extra methods and class to use to generate federation
compliant schemas.

### `toFederatedSchema`

Just like the basic [toSchema](../schema-generator/schema-generator-getting-started.md), `toFederatedSchema` accepts four parameters: `config`, `queries`, `mutations` and `subscriptions`.
The difference is that the `config` class is of type [FederatedSchemaGeneratorConfig](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/federation/FederatedSchemaGeneratorConfig.kt).
This class extends the [base configuration class](../schema-generator/customizing-schemas/generator-config.md) and adds some default logic. You can override the logic if needed, but do so with caution as you may no longer generate a spec compliant schema.

You can see the definition for `toFederatedSchema` [in the
source](https://github.com/ExpediaGroup/graphql-kotlin/blob/3.x.x/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/federation/toFederatedSchema.kt)

## Example

```kotlin

@KeyDirective(fields = FieldSet("id"))
data class User(
  val id: ID,
  val name: String
)

class Query {
  fun getUsers(): List<User> = getUsersFromDB()
}

val config = FederatedSchemaGeneratorConfig(
  supportedPackages = "com.example"
)

toFederatedSchema(
  config = config,
  queries = listOf(TopLevelObject(Query()))
)

```

will generate

```graphql

type Query {
 getUsers: [User!]!
}

type User @key(fields : "id") {
 id: ID!
 name: String!
}

```
