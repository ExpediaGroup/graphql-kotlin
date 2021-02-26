# GraphQL Kotlin Spring Example

One way to run a GraphQL server is with [Spring Boot](https://github.com/spring-projects/spring-boot). This example app uses [Spring Webflux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html) together with `graphql-kotlin-spring-server` and [graphql-playground](https://github.com/prisma/graphql-playground).

### Running locally

First you must build all the other necessary modules since this is a multi-module project.

From the root examples directory you can run the following:

```shell script
# build all examples
./gradlew build

# only build spring-example project
./gradlew :spring-server:build
```

> NOTE: in order to ensure you use the right version of Gradle we highly recommend to use the provided wrapper scripts

Then to start the server:

* Run `Application.kt` directly from your IDE
* Alternatively you can also use the spring boot plugin from the command line in the root examples directory.

```shell script
./gradlew :spring-server:bootRun
```

Once the app has started you can explore the example schema by opening the GraphQL Playground endpoint at http://localhost:8080/playground.
