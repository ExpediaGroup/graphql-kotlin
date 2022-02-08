---
id: annotations
title: Annotations
---
`graphql-kotlin-schema-generator` ships with a number of annotation classes to allow you to enhance your GraphQL schema
for things that can't be directly derived from Kotlin reflection.

- [@GraphQLDescription](./documenting-fields) - Provide a description for a GraphQL field
- [@GraphQLDirective](./directives) - Registers directive on a GraphQL field
- [@GraphQLIgnore](./excluding-fields) - Exclude field from the GraphQL schema
- [@GraphQLName](./renaming-fields) - Override the name used for the type
- Kotlin built in [@Deprecated](./deprecating-schema) - Apply the GraphQL `@deprecated` directive on the field
- [@GraphQLType](./custom-type-reference) - Allows specifying a return type that is not the Kotlin code
