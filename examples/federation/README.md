# Federation Example

This example is two Spring applications `base-app` and `extend-app` that use `graphql-kotlin-federation` to generate the schema.
These apps run on different ports (`8080`, `8081`) so they can run simultaniously.

The `gateway` is a Node.js app running Apollo Gateway on port `4000` and connects to the two Spring apps.
You can make queries against the Spring apps directly or run combined queries from the gateway.

## Running Locally


### Spring Apps
Build the Spring applications by running the following commands in the `/federation` directory

```shell script
./gradlew clean build
```

> NOTE: in order to ensure you use the right version of Gradle we highly recommend to use the provided wrapper scripts

Start the servers:

* Run each `Application.kt` directly from your IDE
* Alternatively you can also use the spring boot plugin from the command line.

```shell script
./gradlew bootRun
```


Once the app has started you can explore the example schema by opening the Playground endpoint
* `base-app` http://localhost:8080/playground
* `extend-app` http://localhost:8081/playground

### Gateway

See the instructions in the gateway [README](./gateway/README.md)
