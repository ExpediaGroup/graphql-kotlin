# GraphQL Kotlin Data Loader Instrumentation
[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-dataloader-instrumentation.svg?label=Maven%20Central)](https://central.sonatype.com/search?namespace=com.expediagroup&q=name%3Agraphql-kotlin-dataloader-instrumentation)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-dataloader-instrumentation.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-dataloader-instrumentation)

`graphql-kotlin-dataloader-instrumentation` is set of custom instrumentations that will signal when is the right moment
to dispatch a `DataLoaderRegistry`.

This instrumentation follows the same approach of the [DataLoaderDispatcherInstrumentation](https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/instrumentation/dataloader/DataLoaderDispatcherInstrumentation.java).

The main difference is that regular `Instrumentation`s are applied by a single `ExecutionInput` aka GraphQL Operation,
whereas these custom instrumentations applies across a number of operations and stores its state in the `GraphQLContext`.

## Install it

Using a JVM dependency manager, link `graphql-kotlin-dataloader-instrumentation` to your project.

With Maven:

```xml
<dependency>
  <groupId>com.expediagroup</groupId>
  <artifactId>graphql-kotlin-dataloader-instrumentation</artifactId>
  <version>${latestVersion}</version>
</dependency>
```

With Gradle (example using kts):

```kotlin
implementation("com.expediagroup:graphql-kotlin-dataloader-instrumentation:$latestVersion")
```

## Use it

When creating your `GraphQL` instance make sure to include an instance of `GraphQLSyncExecutionExhaustedDataLoaderDispatcher`

```kotlin
GraphQL
    .instrumentation(GraphQLSyncExecutionExhaustedDataLoaderDispatcher())
    // configure schema, type wiring, etc.
    .build()
```

When ready to execute an operation or operations create a `GraphQLContext` instance with an instance of
`SyncExecutionExhaustedState`


```kotlin
val queries = [
    """
      query Query1 {
        nasa {
          astronaut(id: 1)
        }
      }
    """,
    """
      query Query1 {
        nasa {
          astronaut(id: 2)
        }
      }
    """
]

val graphQLContext = mapOf(
    SyncExecutionExhaustedState::class to SyncExecutionExhaustedState(queries.size) {
        dataLoaderRegistry
    }
)

val executionInput1 = ExecutionInput.newExecutionInput(queries[0]).graphQLContext(graphQLContext).dataLoaderRegistry(dataLoaderRegistry).build()
val executionInput2 = ExecutionInput.newExecutionInput(queries[1]).graphQLContext(graphQLContext).dataLoaderRegistry(dataLoaderRegistry).build()

val result1 = graphQL.executeAsync(executionInput1)
val result2 = graphQL.executeAsync(executionInput2)
```

the `GraphQLSyncExecutionExhaustedDataLoaderDispatcher` will dispatch the `DataLoaderRegistry` when
  the synchronous execution of an operation exhausted (synchronous execution will be exhausted when all data fetchers
  of all paths executed up until a scalar leaf, or a `CompletableFuture`).

This way even if you are executing 2 separate operations you can still batch operations triggered from a DataFetcher.

### Usage in DataFetcher

To access to a `DataLoader` you can use the `DataFetchingEnvironment.getDataLoader(dataLoaderName: String)` method
which will retrieve target `DataLoader` from the `KotlinDataLoaderRegistry` based on the specified `dataLoaderName`.

```kotlin
class AstronautService {
    fun getAstronaut(
        request: AstronautServiceRequest,
        environment: DataFetchingEnvironment
    ): CompletableFuture<Astronaut> =
        environment
            .getDataLoader<AstronautServiceRequest, Astronaut>("AstronautDataLoader")
            .load(request)
}
```

## Documentation

Additional information can be found in our [documentation](https://opensource.expediagroup.com/graphql-kotlin/docs/server/data-loader/data-loader-instrumentation)
and the [Javadocs](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-data-loader-instrumentation) of all published library versions.

If you have a question about something you can not find in our documentation or javadocs, feel free to [start a new discussion](https://github.com/ExpediaGroup/graphql-kotlin/discussions).
