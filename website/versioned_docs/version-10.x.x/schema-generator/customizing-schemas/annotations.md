---
id: annotations
title: Annotations
---
`graphql-kotlin-schema-generator` ships with a number of annotation classes to allow you to enhance your GraphQL schema
for things that can't be directly derived from Kotlin reflection.

- [@GraphQLDescription](./documenting-schema) - Provide a description for a GraphQL field
- [@GraphQLDirective](./directives) - Registers directive on a GraphQL field
- [@GraphQLIgnore](./excluding-fields) - Exclude field from the GraphQL schema
- [@GraphQLName](./renaming-fields) - Override the name used for the type
- Kotlin built in [@Deprecated](./deprecating-schema) - Apply the GraphQL `@deprecated` directive on the field
- [@GraphQLDeprecated](./deprecating-schema) - Apply the GraphQL `@deprecated` directive but only in the schema, not in your own Kotlin code with `@Deprecated`
- [@GraphQLType](./custom-type-reference) - Allows specifying a return type that is not the Kotlin code
