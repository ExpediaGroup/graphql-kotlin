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

:::note
`_entities` queries are automatically handled by a federated gateway and their usage is transparent for the gateway clients.
:::

## Federated Type Resolver

In order to simplify the integrations, `graphql-kotlin-federation` provides a default `_entities` query data fetcher or resolver that
invokes the [TypeResolver](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/generator/federation/execution/FederatedTypeResolver.kt)
that is used to resolve the specified `__typename`.

`FederatedTypeResolver.typeName` specifies the GraphQL type name that should match the `__typename` field in the `_entities` query.

There are two interfaces that implement the `FederatedTypeResolver`:
1. [FederatedTypeSuspendResolver](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/generator/federation/execution/FederatedTypeSuspendResolver.kt)
2. [FederatedTypePromiseResolver](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/generator/graphql-kotlin-federation/src/main/kotlin/com/expediagroup/graphql/generator/federation/execution/FederatedTypePromiseResolver.kt)

### Federated Type Suspend Resolver

`FederatedTypeSuspendResolver.resolve` receives a representation of the target `__typename` and will execute
the suspending function on a `CoroutineScope` to **asynchronously wait** to complete the target entity or `NULL` if entity cannot be resolved.

`FederatedTypeSuspendResolver.resolve` will be invoked based on how many representations of the target entity were
requested in the `_entities` query.

```kotlin
// This service does not own the "Product" type but is extending it with new fields
@KeyDirective(fields = FieldSet("id"))
@ExtendsDirective
class Product(@ExternalDirective val id: String) {
  fun newField(): String = getNewFieldByProductId(id)
}

// This is how the "Product" class is created from the "_entities" query using suspending resolver
class ProductResolver : FederatedTypeSuspendResolver<Product> {
    override val typeName: String = "Product"

    override suspend fun resolve(
        environment: DataFetchingEnvironment,
        representation: Map<String, Any>
    ): Product? {
        val id = representation["id"]?.toString()
        // Instantiate product using id, otherwise return null
        return if (id != null) {
            Product(id)
        } else {
            null
        }
    }
}
```

:::note
this suspend implementation relies on the same coroutine scope propagation as the
default `FunctionDataFetcher`. See [asynchronous models documentation](../execution/async-models.md) for additional details.
Additionally you can also use `FederatedTypePromiseResolver` which is compatible with `DataLoader`'s async model given that returns
a `CompletableFuture`, that way you get advantage of batching and deduplication of transactions to downstream.
:::

### Federated Type Promise Resolver

`FederatedTypePromiseResolver.resolve` receives a representation of the target `__typename` and provides a `CompletableFuture` of
a nullable instance of target entity.

```kotlin
// This service does not own the "Product" type but is extending it with new fields
@KeyDirective(fields = FieldSet("id"))
@ExtendsDirective
class Product(@ExternalDirective val id: String) {
  fun newField(): String = getNewFieldByProductId(id)
}

// This is how the "Product" class is created from the "_entities" query using promise resolver
class ProductResolver : FederatedTypePromiseResolver<Product> {
    override val typeName: String = "Product"

    override fun resolve(
        environment: DataFetchingEnvironment,
        representation: Map<String, Any>
    ): CompletableFuture<Product?> {
        val id = representation["id"]?.toString()
        // use dataloader to resolve Product by id
        return environment.getDataLoader<String, Product?>("ProductDataLoader").load(id)
    }
}
```

## Provide FederatedTypeResolvers to FederatedSchema

Provide a `List<FederatedTypeResolver>` to the `FederatedSchemaGeneratorHooks` and `graphql-kotlin` will create the
data fetcher or resolver using your custom federated type resolvers

```kotlin
val resolvers = listOf(productResolver)
val hooks = FederatedSchemaGeneratorHooks(resolvers)
val config = FederatedSchemaGeneratorConfig(supportedPackages = listOf("org.example"), hooks = hooks)
val schema = toFederatedSchema(config)
```

:::note
If you are using `graphql-kotlin-spring-server`, each of your FederatedTypeResolvers can be marked as Spring Beans
and will automatically be added to the `FederatedSchemaGeneratorHooks` by using autoconfiguration.
:::
