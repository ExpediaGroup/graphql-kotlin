# How to Contribute

We'd love to accept your patches and contributions to this project. There are just a few guidelines you need to follow which are described in detail below.

## Fork this repo

You should create a fork of this project in your account and work from there. You can create a fork by clicking the fork button in GitHub.

## One feature, one branch

Work for each new feature/issue should occur in its own branch. To create a new branch from the command line:
```shell
git checkout -b my-new-feature
```
where "my-new-feature" describes what you're working on.

## Verify your changes locally

You can use Gradle to build all the modules from the root directory

```shell script
./gradlew clean build
```

Or you can navigate to each module to build them individually.

> NOTE: in order to ensure you use the right version of Gradle we highly recommend to use the provided wrapper scripts

## Add tests for any bug fixes or new functionality

### Unit Tests

We are using [mockk](http://mockk.io), [JUnit](https://junit.org/junit5/), and [jacoco](https://www.eclemma.org/jacoco/) for our main testing libraries. This ensures we have good code coverage and can easily test all cases of schema generation.

To run tests:

```shell script
./gradlew check
```

### Linting
We are using [ktlint](https://ktlint.github.io/) and [detekt](https://arturbosch.github.io/detekt/) for code style checking and linting.

**Note**:
These will be run as part of the `validate` phase of a full build but if you want to run them manually you will have to navigate to each module directory and run the command

```shell script
./gradlew ktlintCheck
```
```shell script
./gradlew detekt
```

## Add documentation for new or updated functionality

Please add appropriate javadocs in the source code and ask the maintainers to update the documentation with any relevant
information.
Further instructions on how to add documentation content are in `website/README.md`.

## Add license information
All source files must contain the following license header. If you are using an IDE please add this as a copyright template for this project so that it will be added automatically.

```
Copyright ${today.year} Expedia, Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 ```

## Merging your contribution

Create a new pull request and your code will be reviewed by the maintainers. They will confirm at least the following:

- Tests run successfully (unit, coverage, integration, style)
- Contribution policy has been followed

A maintainer will need to sign off on your pull request before it can be merged.
