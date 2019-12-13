# Performance Tests

This is a simple performance test script that runs using [Artillery](https://github.com/artilleryio/artillery)

## Local Setup

### Requirements

* Make sure you are using the correct version of [Node](https://nodejs.org/). You can use [NVM](https://github.com/nvm-sh/nvm) to install the version specified in `.nvmrc`

### Running Tests

* Install the dependencies locally to run the tests

    ```shell script
    $ npm install
    ```

* There are multiple performance test cases. To see the availabel tests, run the following command.
    ```shell script
    $ npm run
    ```
* To execute a test simply run the appropiate command through `npm`. Feel free to modify the config for the tests locally as well.
    ```shell script
    $ npm run perf-test-query
    ```
