# GraphQL Kotlin Transaction Batcher
[![Maven Central](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-transaction-batcher.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.expediagroup%22%20AND%20a:%22graphql-kotlin-transaction-batcher%22)
[![Javadocs](https://img.shields.io/maven-central/v/com.expediagroup/graphql-kotlin-transaction-batcher.svg?label=javadoc&colorB=brightgreen)](https://www.javadoc.io/doc/com.expediagroup/graphql-kotlin-transaction-batcher)

`graphql-kotlin-transaction-batcher` is an alternative to `data-loaders`, using reactive stream to apply batching and
deduplication of transactions needed by a DataFetcher.

The implementation is completely agnostic of GraphQL and compatible with any asynchronous approach used to resolve data
as long as you return a `CompletableFuture`, if you are using `coroutines` or `reactive-streams` you can easily apply interoperability.

It also supports caching to avoid making a request to a previously completed transaction, the default caching mechanism is in memory
but this can be easily adapted to use other caching implementations.

## Install it

Using a JVM dependency manager, link `graphql-kotlin-transaction-batcher` to your project.

With Maven:

```xml
<dependency>
  <groupId>com.expediagroup</groupId>
  <artifactId>graphql-kotlin-transaction-batcher</artifactId>
  <version>${latestVersion}</version>
</dependency>
```

With Gradle (example using kts):

```kotlin
implementation("com.expediagroup:graphql-kotlin-transaction-batcher:$latestVersion")
```

## Use it

Assuming we have a service that performs an HTTP Request to a REST endpoint:

```kotlin
class AstronautService {
    fun getAstronaut(request: AstronautServiceRequest): CompletableFuture<Astronaut> {
        // http request to Astronaut API
        // return a CompletableFuture<Astronaut>
    }
}

val astronautService = AstronautService()

val astronaut1 = astronautService.getAstronaut(request1)
val astronaut2 = astronautService.getAstronaut(request2)

```

Create an instance of the [TransactionBatcher](src/main/kotlin/com/expediagroup/graphql/transactionbatcher/transaction/TransactionBatcher.kt)

```kotlin
val transactionBatcher = TransactionBatcher()
```

`TransactionBatcher` takes a [TransactionBatcherCache](src/main/kotlin/com/expediagroup/graphql/transactionbatcher/transaction/cache/TransactionBatcherCache.kt)
implementation as argument in the constructor, by default is [DefaultTransactionBatcherCache](src/main/kotlin/com/expediagroup/graphql/transactionbatcher/transaction/cache/TransactionBatcherCache.kt)
which is just a `ConcurrentHashMap`, you can easily implement your own cache using `caffeine`, `redis`, etc.

Pass the `TransactionBatcher` instance to your service:

```kotlin
class AstronautService(
    private val transactionBatcher: TransactionBatcher
) {
    fun getAstronaut(request: AstronautServiceRequest): CompletableFuture<Astronaut> {
        this.transactionBatcher.batch(request) { requests: List<AstronautServiceRequest> ->
            // http request to Astronaut API
            // should be an endpoint that can resolve multiple astronauts
            // return a reactive-streams Publisher<Astronaut>
        }
    }
}

val astronautService = AstronautService()

val astronaut1 = astronautService.getAstronaut(request1)
val astronaut2 = astronautService.getAstronaut(request2)
```

You don't have to change the return type of `getAstronaut` internally the `TransactionBatcher`
will still return a `CompletableFuture`, and the return type of the second argument of `TransactionBatcher.batch`
has to be a reactive-streams `Publisher`.

Here is where you can use your own httpClient and async approach, coroutines, project reactor, you would just need to
interop to the reactive-streams `Publisher`.

At the moment the returned stream starts to emit data, the previously returned `CompletableFuture` instances will `complete`.

The action that triggers everything is `TransactionBatcher.dispatch`.
