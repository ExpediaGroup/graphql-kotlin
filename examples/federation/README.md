# Federation Example

This example is two Spring applications `base-app` and `extend-app` that use `graphql-kotlin-federation` to generate the schema.
These apps run on different ports (`8080`, `8081`) so they can run simultaniously

Then the `gateway` is a Node.js app running Apollo Gateway on port `4000` and connects to the two spring apps.
You can make queries against the spring apps directly or combined queries from the gateway.

## Running Locally


### Spring Apps
Build the spring applications by running the following commands in the `/federation` directory

```shell script
./gradlew clean build
```

Start the servers:

* Run each `Application.kt` directly from your IDE
* Alternatively you can also use the spring boot plugin from the command line.

```shell script
./gradlew bootRun
```


Once the app has started you can explore the example schema by opening Playground endpoint
* `base-app` http://localhost:8080/playground
* `extend-app` http://localhost:8081/playground

### Gateway

See the instructions in the [README](./gateway/README.md) for this folder
