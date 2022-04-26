---
id: data-loader-instrumentation
title: Data Loader Instrumentations
---

`graphql-kotlin-dataloader-instrumentation` is set of custom [Instrumentations](https://www.graphql-java.com/documentation/instrumentation/)
that will calculate when is the right moment to dispatch `KotlinDataLoader`s across single and batch GraphQL operations.

These custom instrumentations follow the similar approach as the default [DataLoaderDispatcherInstrumentation](https://github.com/graphql-java/graphql-java/blob/master/src/main/java/graphql/execution/instrumentation/dataloader/DataLoaderDispatcherInstrumentation.java)
from `graphql-java`, the main difference is that regular instrumentations apply to a single `ExecutionInput` aka [GraphQL Operation](https://www.graphql-java.com/documentation/execution#queries),
whereas these custom instrumentations apply to multiple GraphQL operations (say a BatchRequest) and stores their state in the `GraphQLContext`.
allowing batching and deduplication of transactions across those multiple GraphQL operations.

By default, each GraphQL operation is processed independently of each other. Multiple operations can be processed
together as if they were single GraphQL request if they are part of the same batch request.

The `graphql-kotlin-dataloader-instrumentation` module contains 2 custom `DataLoader` instrumentations:

1. **DataLoaderLevelDispatchedInstrumentation**

   Dispatches all data loaders when a certain **Level** of all **ExecutionInputs** sharing a
   **GraphQLContext** was dispatched (all DataFetchers were invoked).

2. **DataLoaderSyncExecutionExhaustedInstrumentation**

   Dispatches all data loaders when all synchronous execution paths across all operations are exhausted. Synchronous execution path
   is considered exhausted when all currently processed data fetchers were either resolved to a scalar or dispatched a `CompletableFuture`.

## Dispatching by level

The `DataLoaderLevelDispatchedInstrumentation` tracks the state of all `ExecutionInputs` across operations. When a certain
field dispatches, it will check if all fields across all operations for a particular level were dispatched and if the condition is met,
it will dispatch all the data loaders.

### Example

**Queries**

```graphql
query Q1 {
    astronaut(id: 1) { # async
        id
        name
        missions { # async
            id
            designation
        }
    }
}

query Q2 {
    astronaut(id: 2) { # async
        id
        name
        missions { # async
            id
            designation
        }
    }
}
```

**Execution diagram**

![Image of data loader level dispatched instrumentation](../../assets/data-loader-level-dispatched-instrumentation.png)

* The `astronaut` `DataFetcher` uses a `AstronautDataLoader` which will be dispatched when **Level 1** of those 2 operations
  is dispatched, causing the `AstronautDataLoader` to load 2 astronauts.
* The `missions` `DataFetcher` uses a `MissionsByAstronautDataLoader` which will be dispatched when **Level 2** of those 2 operations
  is dispatched, causing the `MissionsByAstronautDataLoader` to load 2 lists of missions by astronaut.

### Usage on spring server

```yaml
graphql:
  batching:
   enabled: true
   strategy: LEVEL_DISPATCHED
```

this configuration will add a `DataLoaderLevelDispatchedInstrumentation` instance to the `GraphQL` instance.

You can find additional examples in our [unit tests](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/executions/graphql-kotlin-dataloader-instrumentation/src/test/kotlin/com/expediagroup/graphql/dataloader/instrumentation/level/DataLoaderLevelDispatchedInstrumentationTest.kt).

### Limitations

This instrumentation is a good option if your **GraphQLServer** will receive a batched request with operations of the same type,
in those cases batching by level is enough, however, this solution is far from being the most optimal as we don't necessarily want to dispatch by level.

## Dispatching by synchronous execution exhaustion

The most optimal time to dispatch all data loaders is when all possible synchronous execution paths across all batch
operations were exhausted. Synchronous execution path is considered exhausted (or completed) when all currently processed
data fetchers were either resolved to a scalar or a future promise.

Let's analyze how GraphQL execution works, but first lets check some GraphQL concepts:

**DataFetcher**

Each field in GraphQL has a resolver aka `DataFetcher` associated with it, some fields will use specialized `DataFetcher`s
that knows how to go to a database or make a network request to get field information while most simply take
data from the returned memory objects.

Note: `DataFetcher`s are some times called "resolvers" in other graphql implementations.


**Execution Strategy**

The process of finding values for a list of fields from the GraphQL Query, using a recursive strategy.

### Example

**Queries**

```graphql
query Q1 {
    astronaut(id: 1) { # async
        id
        name
        missions { # async
            id
            designation
        }
    }
}

query Q2 {
    nasa { #sync
        astronaut(id: 2) { # async
            id
            name
            missions { # async
                id
                designation
            }
        }
        address { # sync
            street
            zipCode
        }
        phoneNumber
    }
}
```

**The order of execution of the queries will be:**

***for Q1***
1. Start an `ExecutionStrategy` for the `root` field of the query, to concurrently resolve `astronaut` field.
    * `astronaut` **DataFetcher** will invoke the `AstronautDataLoader` and will return a `CompletableFuture<Astronaut>` so we can consider this path exhausted.

***for Q2***
1. Start an `ExecutionStrategy` for the `root` field of the query, to concurrently resolve `nasa` field.
    * `nasa` **DataFetcher** will synchronously return a `Nasa` object, so we can descend more that path.
2. Start an `ExecutionStrategy` for the `nasa` field of the `root` field of the query to concurrently resolve `astronaut`, `address` and `phoneNumber`.
    * `astronaut` **DataFetcher**  will invoke the `AstronautDataLoader` and will return a `CompletableFuture<Astronaut>` so we can consider this path exhausted
    * `address` **DataFetcher** will synchronously return an `Address` object, so we can descend more that path.
    * `phoneNumber` **DataFetcher** will return a scalar, so we can consider this path exhausted.
3. Start an `ExecutionStrategy` for the `address` field of the `nasa` field to concurrently resolve `street` and `zipCode`.
    * `street` **DataFetcher** will return a scalar, so we can consider this path exhausted.
    * `zipCode` **DataFetcher** will return a scalar, so we can consider this path exhausted.

**At this point we can consider the synchronous execution exhausted and the `AstronautDataLoader` has 2 keys to be dispatched,
if we proceed dispatching all data loaders the execution will continue as following:**

***for Q1***
1. Start and `ExecutionStrategy` for the `astronaut` field of the `root` field of the query to concurrently resolve `id`, `name` and `mission` fields.
    * `id` **DataFetcher** will return a scalar, so we can consider this path exhausted.
    * `name` **DataFetcher** will return a scalar, so we can consider this path exhausted.
    * `missions` **DataFetcher** will invoke the `MissionsByAstronautDataLoader` and will return a `CompletableFuture<List<Mission>>` so we can consider this path exhausted.

***for Q2***
1. Start and `ExecutionStrategy` for the `astronaut` field of the `nasa` field of the query to concurrently resolve `id`, `name` and `mission` fields.
    * `id` **DataFetcher** will return a scalar, so we can consider this path exhausted.
    * `name` **DataFetcher** will return a scalar, so we can consider this path exhausted.
    * `missions` **DataFetcher** will invoke the `MissionsByAstronautDataLoader` and will return a `CompletableFuture<List<Mission>>` so we can consider this path exhausted.

**At this point we can consider the synchronous execution exhausted and the `MissionsByAstronautDataLoader` has 2 keys to be dispatched,
if we proceed dispatching all data loaders the execution will continue to just resolve scalar fields.**

![Image of data loader level dispatched instrumentation](../../assets/data-loader-level-sync-execution-exhausted-instrumentation.png)

### Usage on spring server
```yaml
graphql:
  batching:
    enabled: true
    strategy: SYNC_EXHAUSTION
```

You can find additional examples in our [unit tests](https://github.com/ExpediaGroup/graphql-kotlin/blob/master/executions/graphql-kotlin-dataloader-instrumentation/src/test/kotlin/com/expediagroup/graphql/dataloader/instrumentation/syncexhaustion/DataLoaderSyncExecutionExhaustedInstrumentationTest.kt).
