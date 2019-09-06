# Contributing

`graphql-kotlin` is open source and welcomes contributions. We do ask that you help us maintain a clean library and create the best code for everyone to use.

## Build
You can use Maven to build all the modules from the root directory

```shell script
mvn clean install
```

Or you can navigate to each module to build them

## Testing

### Unit Tests

We are using [mockk](http://mockk.io), [JUnit](https://junit.org/junit5/), and [jacoco](https://www.eclemma.org/jacoco/) for our main testing libraries. This ensures we have good code coverage and can easily test all cases of schema generation.

To run tests use Maven

```shell script
mvn verify
```

You can also view the code coverage reports published to Codecov. Links in the README

### Linting
We are also [ktlint](https://ktlint.github.io/) and [detekt](https://arturbosch.github.io/detekt/) for code style checking and linting. These can be run wiht the following Maven commands

**Note**:
These will be run as part of the `validate` phase of a full build but if you want to run them manually you will have to navigate to each module directory and run the command

```shell script
mvn antrun:run@ktlint
```
```shell script
mvn antrun:run@detekt
```

## License

See [LICENSE](LICENSE)
