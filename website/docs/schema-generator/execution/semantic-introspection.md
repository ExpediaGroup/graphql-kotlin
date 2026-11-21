---
id: semantic-introspection
title: Semantic Introspection
---
:::caution
Semantic introspection is experimental. It follows the GraphQL AI working group's
[Semantic Introspection RFC](https://github.com/graphql/ai-wg/blob/main/rfcs/semantic-introspection.md), which is not
final, so its API may change in a minor release. Opt in with `@OptIn(ExperimentalSemanticIntrospection::class)`.
:::

Large schemas are too big to hand to an LLM. Semantic introspection lets an agent ask the server which schema members
match a natural language question, then read only those through standard introspection.

```graphql
{
  _search(query: "what are the releases of a repository?", first: 3) {
    coordinate
    score
    pathsToRoot
  }
}
```

```json
{
  "data": {
    "_search": [
      {
        "coordinate": "Repository.releases",
        "score": 0.92,
        "pathsToRoot": [["Query.repository", "Repository.releases"]]
      }
    ]
  }
}
```

## Resolving searches

GraphQL Kotlin adds the field and its type; the application ranks the schema by implementing `SemanticSearchResolver`.

```kotlin
@OptIn(ExperimentalSemanticIntrospection::class)
class SchemaSearch : SemanticSearchResolver {
    override suspend fun search(environment: DataFetchingEnvironment, request: SemanticSearchRequest): List<SemanticSearchResult> {
        val schema = environment.graphQLSchema
        // rank schema coordinates for request.query, e.g. with an index built once per schema
        return listOf(
            SemanticSearchResult(
                coordinate = "Repository.releases",
                pathsToRoot = listOf(listOf("Query.repository", "Repository.releases")),
                score = 0.92
            )
        )
    }
}
```

The resolver owns ranking:

- return at most `request.first` results, best match first, each scoring at least `request.minScore` when it is set
- return [schema coordinates](https://spec.graphql.org/draft/#sec-Schema-Coordinates) of visible schema members
  (`Type`, `Type.field`, `Input.field`, `Enum.VALUE`, `Type.field(arg:)`, `@directive`, `@directive(arg:)`), and
  `pathsToRoot` as lists of `Type.field` coordinates from a root field. Results that break these rules fail the field rather than being trimmed;
  paths through fields hidden by field visibility are dropped
- read the schema from `environment.graphQLSchema`, not by injecting it: the search is part of the schema it searches
- throw a `GraphQLError` (for example `GraphqlErrorException`) for an error meant for the client; other exceptions go to
  the `DataFetcherExceptionHandler`
- apply its own limits on query length, timeouts and concurrency when it calls a model or another service

The resolver runs in the request's `CoroutineScope` when the GraphQL context has one, like suspend data fetchers.

## Enabling it

With the Spring server, register a `SemanticSearchResolver` bean. `Query._search` is added to the schema bean unless
`graphql.introspection.enabled` is `false`.

Without Spring, apply it to the final schema:

```kotlin
val schema = toSchema(config, queries).withSemanticIntrospection(SchemaSearch())
```

The Ktor server builds its schema internally and does not support semantic introspection yet.

Like introspection, the field honors `Introspection.enabledJvmWide(false)` and the per request
`Introspection.INTROSPECTION_DISABLED` context flag, and `withSemanticIntrospection` refuses schemas built with
introspection disabled.

## Differences from the RFC

- **Names**: the RFC's `__search` and `__SearchResult` are `_search` and `_SearchResult`. graphql-java reserves names
  starting with `__` for its built-in introspection.
- **Results are coordinates only**: there is no `definition` field and no `__definitions` lookup. The RFC's definition
  union of introspection types does not survive an introspection round trip in graphql-java tooling. Read definitions
  with standard introspection, for example `__type(name: "Repository") { fields { name description } }`.
- **No pagination**: there is no `after` argument or `cursor` field; ask for more results with `first`.
- **Nullable list**: `_search` returns `[_SearchResult!]`, so a failed search nulls only `_search`.

## Printed SDL and federation

`_search` belongs to the running service, not to schemas composed or stitched from SDL. The `print()` extension leaves
it out unless introspection types are printed, and for federated services `_service { sdl }` never includes it. Query a
subgraph directly to search it; a federated router does not expose `_search`.
