# GraphQL Kotlin Spark HTTP

[Spark](http://sparkjava.com/) is a simple HTTP server library

### Running locally
Build the application by running the following from examples root directory:

```bash
# build example
./gradlew build
```

> NOTE: in order to ensure you use the right version of Gradle we highly recommend that you use the provided wrapper scripts

Start the server by running `Application.kt` directly from your IDE. (Make sure that your Kotlin Compiler jvm target is 1.8 or greater.)
Alternatively, you can start the server using Gradle.

```bash
cd /path/to/graphql-kotlin/examples
./gradlew run
```

Once the app has started you can explore the example schema by opening Playground endpoint at http://localhost:5000/graphql

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
