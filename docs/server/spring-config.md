---
id: spring-config
title: Spring Configuration
---

`graphql-kotlin-spring-server` uses ConfigurationProperties to provide various customizations of the auto-configuration
library. All applicable configuration properties expose [configuration
metadata](https://docs.spring.io/spring-boot/docs/current/reference/html/configuration-metadata.html) that provides
details on the supported configuration properties.

## Configuration Properties

| Property | Description | Default Value |
|----------|-------------|---------------|
| graphql.endpoint | GraphQL server endpoint | "graphql" |
| graphql.packages | List of supported packages that can contain GraphQL schema type definitions | |
| graphql.federation.enabled | Boolean flag indicating whether to generate federated GraphQL model | |
| graphql.subscriptions.endpoint | GraphQL subscriptions endpoint | "subscriptions" |
