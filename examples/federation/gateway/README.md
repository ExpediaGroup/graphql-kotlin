# Apollo Federation

This is a simple Apollo Federation Gateway server. For more info see the Apollo docs

https://www.apollographql.com/docs/apollo-server/federation/introduction/

## Setup

Install the correct version of Node locally. You can also use [nvm](https://github.com/nvm-sh/nvm)

```shell script
nvm i
```

```shell script
npm install
```

Then start the other applications

* `base-app` should run on port 8080
* `extend-app` should run on port 8081

Then you can start the gateway
```shell script
npm start
```
