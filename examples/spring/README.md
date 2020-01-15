# GraphQL Kotlin Spring Example

One way to run a GraphQL server is with [Spring Boot](https://github.com/spring-projects/spring-boot). This example app uses [Spring Webflux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html) together with `graphql-kotlin` and [graphql-playground](https://github.com/prisma/graphql-playground).

### Running locally

First you must build all the other modules since this is a multi-module project.

From the root directory:

```shell script
gradle build
```

Then to start the server:

* Run `Application.kt` directly from your IDE
* Alternatively you can also use the spring boot maven plugin by running `gradle bootRun` from the command line in the spring example directory.

Once the app has started you can explore the example schema by opening the GraphQL Playground endpoint at http://localhost:8080/playground.
