---
id: annotations
title: Annotations
original_id: annotations
---
`graphql-kotlin-schema-generator` ships with a number of annotation classes to allow you to enhance your GraphQL schema
for things that can't be directly derived from Kotlin reflection.

-   [@GraphQLDescription](../customizing-schemas/documenting-fields) - Provide a description for a GraphQL field
-   [@GraphQLDirective](../customizing-schemas/directives) - Registers directive on a GraphQL field
-   [@GraphQLIgnore](../customizing-schemas/excluding-fields) - Exclude field from the GraphQL schema
-   [@GraphQLName](../customizing-schemas/renaming-fields) - Override the name used for the type
-   Kotlin built in [@Deprecated](../customizing-schemas/deprecating-schema) - Apply the GraphQL `@deprecated` directive on the field
