# GraphQL Kotlin Spring Example

One way to run a GraphQL server is with [Spring Boot](https://github.com/spring-projects/spring-boot). This example app uses [Spring Webflux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html) together with `graphql-kotlin` and [graphql-playground](https://github.com/prisma/graphql-playground).

### Running locally
Build the application

```bash
gradle build
```

Start the server:

* Run `Application.kt` directly from your IDE
* Alternatively you can also use the spring boot maven plugin by running `gradle bootRun` from the command line.


Once the app has started you can explore the example schema by opening Playground endpoint at http://localhost:8080/playground.
