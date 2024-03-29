---
id: type-resolution
title: Federated Type Resolution
---
In traditional (i.e. non-federated) GraphQL servers, each one of the output types is accessible through a traversal of
the GraphQL schema from a corresponding query, mutation or subscription root type. Since federated GraphQL types might
be accessed outside of the query path we need a mechanism to access them in a consistent manner.

## `_entities` query

A federated GraphQL server provides a custom `_entities` query that allows retrieving any of the federated extended types.
The `_entities` query accept list of "representation" objects that provide all required fields to resolve the type and
return an `_Entity` union type of all supported federated types. Representation objects are just a map of all the fields
referenced in `@key` directives as well as the target `__typename` information. If federated query type fragments also
reference fields with `@requires` and `@provides` directives, then those referenced fields should also be specified in
the target representation object.

:::note
`_entities` queries are automatically handled by the federated gateway and their usage is transparent for the gateway clients.
`EntityResolver` provided by the `graphql-kotlin-federation` module relies on the same coroutine scope propagation as the
default `FunctionDataFetcher`. See [asynchronous models documentation](../execution/async-models.md) for additional details.
:::

```graphql
query ($_representations: [_Any!]!) {
  _entities(representations: $_representations) {
    ... on SomeFederatedType {
      fieldA
      fieldB
    }
  }
}
```

### Federated Type Resolver

In order to simplify the integrations, `graphql-kotlin-federation` provides a default `_entities` query resolver that
retrieves the
[FederatedTypeResolver](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/generator/federation/execution/FederatedTypeResolver.kt)
that is used to resolve the specified `__typename`.

`FederatedTypeResolver.typeName` specifies the GraphQL type name that should match up to the `__typename` field in the `_entities` query.

`FederatedTypeResolver.resolve` accepts a list of representations of the target types which should be resolved in the same order
as they were specified in the list of representations. Each passed in representation should either be resolved to a
target entity or `NULL` if entity cannot be resolved.

```kotlin
// This service does not own the "Product" type but is extending it with new fields
@KeyDirective(fields = FieldSet("id"))
@ExtendsDirective
class Product(@ExternalDirective val id: String) {
  fun newField(): String = getNewFieldByProductId(id)
}

// This is how the "Product" class is created from the "_entities" query
class ProductResolver : FederatedTypeResolver<Product> {
    override val typeName: String = "Product"

    override suspend fun resolve(representations: List<Map<String, Any>>): List<Product?> = representations.map {
        val id = it["id"]?.toString()

        // Instantiate product using id, otherwise return null
        if (id != null) {
            Product(id)
        } else {
            null
        }
    }
}

// If you are using "graphql-kotlin-spring-server", your FederatedTypeResolvers can be marked as Spring beans
// and will automatically be added to the hooks
val resolvers = listOf(productResolver)
val hooks = FederatedSchemaGeneratorHooks(resolvers)
val config = FederatedSchemaGeneratorConfig(supportedPackages = listOf("org.example"), hooks = hooks)
val schema = toFederatedSchema(config)
```
