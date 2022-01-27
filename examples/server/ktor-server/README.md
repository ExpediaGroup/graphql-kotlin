# GraphQL Kotlin Ktor

[Ktor](http://ktor.io/) is an asynchronous framework for creating microservices, web applications, and more.

### Running locally
Build the application by running the following from examples root directory:

```bash
# build example
./gradlew ktor-server:build
```

> NOTE: in order to ensure you use the right version of Gradle we highly recommend that you use the provided wrapper scripts

Start the server by running `Application.kt` directly from your IDE. (Make sure that your Kotlin Compiler jvm target is 1.8 or greater.)
Alternatively, you can start the server using Gradle.

```bash
cd /path/to/graphql-kotlin/examples
./gradlew ktor-server:run
```

Once the app has started you can:
- send GraphQL requests by opening the Playground endpoint at http://localhost:8080/playground
- send GraphQL requests directly to the endpoint at http://localhost:8080/graphql
- explore and interact with the example schema by opening the Playground IDE endpoint at http://localhost:8080/playground

#### Example query

You can use the following example query to view several of the related models:

```graphql
query {
  searchCourses(params: { ids: [1,2,3] }) {
    id
    name
    books {
      title
    }
    university {
      id
      name
    }
  }

  searchUniversities(params: { ids: [1]}) {
    id
    name
  }
}
```

You can also query the `longThatNeverComes` field from several of the types. This will throw and exception,
allow you to see how the `ExecutionStrategy` handles throw exceptions in different levels of the query.
