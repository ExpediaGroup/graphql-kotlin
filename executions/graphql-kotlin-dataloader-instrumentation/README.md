# GraphQL Kotlin Data Loader Instrumentation
[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-transaction-batcher-instrumentation.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.expediagroup%22%20AND%20a:%22graphql-kotlin-transaction-batcher-instrumentation%22)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-transaction-batcher-instrumentation.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-transaction-batcher-instrumentation)

`graphql-kotlin-dataloader-instrumentation` is set of custom instrumentations that will signal when is the right moment
to dispatch a `KotlinDataLoaderRegistry` located in the `GraphQLContext`.

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

When creating your `GraphQL` instance make sure to include either
`DataLoaderLevelDispatchedInstrumentation` or `DataLoaderSyncExhaustionInstrumentation`.

```kotlin
GraphQL
    .instrumentation(DataLoaderSyncExhaustionInstrumentation())
    // configure schema, type wiring, etc.
    .build()
```

When ready to execute an operation or operations make sure to create a single instance of `KotlinDataLoaderRegistry`
and an instance of either `ExecutionLevelInstrumentationState` or `SyncExhaustionInstrumentationState`
and store them in the `graphQLContext`.

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
             astronaut(id: 1)
           }
         }
    """
]

val graphQLContext = mapOf(
    KotlinDataLoaderRegistry::class to kotlinDataLoaderRegistry,
    SyncExhaustionInstrumentationState::class to SyncExhaustionInstrumentationState(
        queries.size,
        kotlinDataLoaderRegistry
    )
)

val executionInput1 = ExecutionInput.newExecutionInput(queries[0]).graphQLContext(graphQLContext).build()
val executionInput2 = ExecutionInput.newExecutionInput(queries[1]).graphQLContext(graphQLContext).build()

val result1 = graphQL.executeAsync(executionInput1)
val result2 = graphQL.executeAsync(executionInput2)
```

- `DataLoaderLevelDispatchedInstrumentation` will dispatch the `KotlinDataLoaderRegistry` instance when
  a certain level of all executionInputs was dispatched (all DataFetchers were invoked).
- `DataLoaderSyncExhaustionInstrumentation` will dispatch the `KotlinDataLoaderRegistry` instance when
  the synchronous execution of an operation exhausted (synchronous execution will be exhausted when all data fetchers
  of all paths executed up until a scalar leaf, or a [CompletableFuture]).

This way even if you are executing 2 separate operations you can still batch operations triggered from a DataFetcher.

### Usage in DataFetcher

You can use the `DataFetchingEnvironment` which is passed to each
`DataFetcher` and invoke the `getDataLoaderFromContext(dataLoaderName)` method which will access to the `KotlinDataLoaderRegistry`
in the `GraphQLContext` and provide the `DataLaoder` that you specified as `dataLoaderName` argument.

```kotlin
class AstronautService {
    fun getAstronaut(
        request: AstronautServiceRequest,
        environment: DataFetchingEnvironment
    ): CompletableFuture<Astronaut> =
        environment
            .getDataLoaderFromContext<AstronautServiceRequest, Astronaut>("AstronautDataLoader")
            .load(request)
}
```



