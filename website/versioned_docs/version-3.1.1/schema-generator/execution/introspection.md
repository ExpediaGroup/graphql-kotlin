---
id: version-3.1.1-introspection
title: Introspection
original_id: introspection
---
By default, GraphQL servers expose a built-in system, called **introspection**, that exposes details about the underlying schema.
Clients can use introspection to obtain information about all the supported queries as well as all the types exposed in the schema.

## Introspection types

* ___schema_ - root level query field that provides information about all entry points (e.g. `queryType`), all types exposed
by the schema (including built-in scalars and introspection types) as well as all directives supported by the system
* ___type(name: String!)_ - root level query field that provides information about the requested type (if it exists)
* ___typename_ - field that can be added to *ANY* selection and will return the name of the enclosing type, `__typename`
is often used in polymorphic queries in order to easily determine underlying implementation type
* ___Directive_, ___DirectiveLocation_, ___EnumValue_, ___Field_, ___InputValue_, ___Schema_, ___Type_, ___TypeKind_ - built-in
introspection types that are used to describe the schema.

For example, the query below will return a root Query object name as well as names of all types and all directives.

```graphql
query {
  __schema {
    queryType {
      name
    }
    types {
      name
    }
    directives {
      name
    }
  }
}
```

Additional information on introspection can be found on [GraphQL.org](https://graphql.org/learn/introspection/).

## Disabling Introspection

Introspection system can be disabled by specifying `introspectionEnabled=false` configuration option on an instance of
`SchemaGeneratorConfig` that will be used by the `SchemaGenerator` to generate the GraphQL schema.

Many GraphQL tools (e.g. [GraphQL Playground](https://github.com/prisma-labs/graphql-playground) or [GraphiQL](https://github.com/graphql/graphiql))
rely on introspection queries to function properly. Disabling introspection will prevent clients from accessing `__schema`
and `__type` fields. This may break some of the functionality that your clients might rely on and should be used with
extreme caution.
