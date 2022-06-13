# GraphQL Kotlin Data Loader
[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-dataloader.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.expediagroup%22%20AND%20a:%22graphql-kotlin-dataloader%22)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-dataloader.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-dataloader)

Data Loaders are a popular caching pattern from the [JavaScript GraphQL implementation](https://github.com/graphql/dataloader).

Since `graphql-java` provides [support for this pattern](https://www.graphql-java.com/documentation/batching/)
using the [DataLoader](https://github.com/graphql-java/java-dataloader/blob/master/src/main/java/org/dataloader/DataLoader.java)
and [DataLoaderRegistry](https://github.com/graphql-java/java-dataloader/blob/master/src/main/java/org/dataloader/DataLoaderRegistry.java).

`graphql-kotlin` provides support for `DataLoaders` with this `graphql-kotlin-dataloader` module through common interfaces.


## KotlinDataLoader

To help in the registration of  `DataLoaders`, we have created a basic interface `KotlinDataLoader`:

```kotlin
interface KotlinDataLoader<K, V> {
    val dataLoaderName: String
    fun getDataLoader(): DataLoader<K, V>
}
```

This allows for library users to still have full control over the creation of the DataLoader
and its various configuration options.

```kotlin
class UserDataLoader : KotlinDataLoader<ID, User> {
    override val dataLoaderName = "UserDataLoader"
    override fun getDataLoader() = DataLoaderFactory.newDataLoader<ID, User>(
        { ids ->
            CompletableFuture.supplyAsync {
                ids.map { id -> userService.getUser(id) }
            }
        },
        DataLoaderOptions.newOptions().setCachingEnabled(false)
    )
}
```

## KotlinDataLoaderRegistryFactory

Factory that facilitates the instantiation of a [KotlinDataLoaderRegistry](src/main/kotlin/com/expediagroup/graphql/dataloader/KotlinDataLoaderRegistry.kt) which is just
a decorator of the original `graphql-java` [DataLoaderRegistry](https://github.com/graphql-java/java-dataloader/blob/master/src/main/java/org/dataloader/DataLoaderRegistry.java).
with the addition of allowing access to the state of the `CacheMap` (futures cache) of each `DataLoader` in order to know
all futures state.

## Install it

Using a JVM dependency manager, link `graphql-kotlin-dataloader` to your project.

With Maven:

```xml
<dependency>
  <groupId>com.expediagroup</groupId>
  <artifactId>graphql-kotlin-dataloader</artifactId>
  <version>${latestVersion}</version>
</dependency>
```

With Gradle (example using kts):

```kotlin
implementation("com.expediagroup:graphql-kotlin-dataloader:$latestVersion")
```

## Use it

Use `KotlinDataLoaderRegistryFactory`

```kotlin
    val kotlinDataLoaderRegistry = KotlinDataLoaderRegistryFactory(
        UserDataLoader()
    ).generate()

    val executionInput = ExecutionInput.newExecutionInput()
        .query("query MyAwesomeQuery { foo { bar } }")
        .dataLoaderRegistry(kotlinDataLoaderRegistry)
        .build()

    val result = graphQL.executeAsync(executionInput)
```
