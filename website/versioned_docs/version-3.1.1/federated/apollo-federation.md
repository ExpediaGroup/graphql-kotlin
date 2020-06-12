---
id: version-3.1.1-apollo-federation
title: Apollo Federation
original_id: apollo-federation
---

In many cases, exposing single GraphQL API that exposes unified view of all the available data provides tremendous value
to their clients. As the underlying graph scales, managing single monolithic GraphQL server might become less and less
feasible making it much harder to manage and leading to unnecessary bottlenecks. Migrating towards federated model with
an API gateway and a number of smaller GraphQL services behind it alleviates some of those problems and allows teams to
scale their graphs more easily.

[Apollo Federation](https://www.apollographql.com/docs/apollo-server/federation/introduction/) is an architecture for 
composing multiple GraphQL services into a single graph. Federated schemas rely on a number of custom directives to 
instrument the behavior of the underlying graph and convey the relationships between different schema types. Each individual 
GraphQL server generates a valid GraphQL schema and can be run independently. This is in contrast with traditional schema 
stitching approach where relationships between individual services, i.e. linking configuration, is configured at the GraphQL 
Gateway level.
