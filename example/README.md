# graphql-kotlin example

One way to run a GraphQL server is with spring boot. This example app uses `graphql-kotlin`, `graphql-java-servlet` and `graphiql`.


### Running locally
Build the application

```bash
mvn clean install
```

Start the server:

* Run `Application.kt` directly from your IDE
* Alternatively you can also use the spring boot maven plugin by running `mvn spring-boot:run` from the command line.


Once the app has started you can explore the example schema by opening GraphiQL endpoint at http://localhost:8080/graphiql.
