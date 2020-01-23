# GraphQL Kotlin Spring Example

One way to run a GraphQL server is with [Spring Boot](https://github.com/spring-projects/spring-boot). This example app uses [Spring Webflux](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html) together with `graphql-kotlin` and [graphql-playground](https://github.com/prisma/graphql-playground).

### Running locally
Build the application by running the following from examples root directory:

```bash
# build all examples
./gradlew build

# only build federation extended app project
./gradlew :federation-example:base-app
```

> NOTE: in order to ensure you use the right version of Gradle we highly recommend to use the provided wrapper scripts

Start the server:

* Run `Application.kt` directly from your IDE
* Alternatively you can also use the spring boot plugin from the command line in the root examples directory.

```shell script
./gradlew ::federation-example:base-app:bootRun
```


Once the app has started you can explore the example schema by opening Playground endpoint at http://localhost:8080/playground.
