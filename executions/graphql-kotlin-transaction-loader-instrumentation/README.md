# GraphQL Kotlin Transaction Batcher Instrumentation
[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-transaction-batcher-instrumentation.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.expediagroup%22%20AND%20a:%22graphql-kotlin-transaction-batcher-instrumentation%22)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-transaction-batcher-instrumentation.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-transaction-batcher-instrumentation)

`graphql-kotlin-transaction-batcher-instrumentation` is a custom instrumentation that will signal when is the right moment
to dispatch transactions added in the `TransactionBatcher` located in the `GraphQLContext`.

This instrumentation follows the same approach of the [DataLoaderDispatcherInstrumentation](https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/instrumentation/dataloader/DataLoaderDispatcherInstrumentation.java).

The main difference is that regular `Instrumentation`s are applied by a single `ExecutionInput` aka GraphQL Operation,
whereas this new instrumentation applies across a number of operations and stores its state in the `GraphQLContext`.

Once a certain level of all executions in the `GraphQLContext` dispatched we signal the `TransactionBatcher` to dispatch.

## Install it

Using a JVM dependency manager, link `graphql-kotlin-transaction-batcher-instrumentation` to your project.

With Maven:

```xml
<dependency>
  <groupId>com.expediagroup</groupId>
  <artifactId>graphql-kotlin-transaction-batcher-instrumentation</artifactId>
  <version>${latestVersion}</version>
</dependency>
```

With Gradle (example using kts):

```kotlin
implementation("com.expediagroup:graphql-kotlin-transaction-batcher-instrumentation:$latestVersion")
```

## Use it

When creating your `GraphQL` instance make sure to include the `TransactionBatcherLevelInstrumentation`.

```kotlin
GraphQL
    .instrumentation(TransactionBatcherLevelInstrumentation())
    // configure schema, type wiring, etc.
    .build()
```

When ready to execute an operation or operations make sure to create a single instance of `TransactionBatcher`
and `ExecutionLevelInstrumentationState` and store them in the `graphQLContext`.

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
    TransactionBatcher::class to transactionBatcher,
    ExecutionLevelInstrumentationState::class to ExecutionLevelInstrumentationState(queries.size)
)

val executionInput1 = ExecutionInput.newExecutionInput(queries[0]).graphQLContext(graphQLContext).build()
val executionInput2 = ExecutionInput.newExecutionInput(queries[1]).graphQLContext(graphQLContext).build()

val result1 = graphQL.executeAsync(executionInput1)
val result2 = graphQL.executeAsync(executionInput2)
```

`TransactionBatcherLevelInstrumentation` will detect when a certain level of all executionInputs was dispatched (DataFetcher was called)
and then will automatically dispatch the instance of `TransactionBatcher` in the `GraphQLContext`.

This way even if you are executing 2 separate operations you can still batch the requests to the Astronaut API.

### Usage in DataFetcher

In order to access to the `TransactionBatcher` instance, you can use the `DataFetchingEnvironment` which is passed to each
`DataFetcher`

```kotlin
class AstronautService {
    fun getAstronaut(
        request: AstronautServiceRequest,
        environment: DataFetchingEnvironment
    ): CompletableFuture<Astronaut> =
        environment.transactionBatcher().batch(request) { requests: List<AstronautServiceRequest> ->
            // perform Transaction with list of requests and return a Publisher<Astronaut>
        }
}
```



