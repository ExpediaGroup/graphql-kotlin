const { ApolloServer } = require("apollo-server");
const { ApolloGateway, RemoteGraphQLDataSource } = require("@apollo/gateway");

const server = new ApolloServer({
  gateway: new ApolloGateway({
    debug: true,
    serviceList: [
      { name: "base-app", url: "http://localhost:8080/graphql" },
      { name: "extend-app", url: "http://localhost:8081/graphql" }
    ]
  }),
  subscriptions: false
});

server.listen().then(({ url }) => {
  console.log(`🚀 Server ready at ${url}`);
});
