# Apollo Federation Subgraph Compatibility

This is a reference implementation used for testing `graphql-kotlin` compatibility against [Apollo Federation Subgraph Specification](https://www.apollographql.com/docs/federation/subgraph-spec/).

This project implements following reference schema:

```graphql
extend schema
  @link(
    url: "https://specs.apollo.dev/federation/v2.0",
    import: [
      "@extends",
      "@external",
      "@key",
      "@inaccessible",
      "@override",
      "@provides",
      "@requires",
      "@shareable",
      "@tag"
    ]
  )

type Product
  @key(fields: "id")
  @key(fields: "sku package")
  @key(fields: "sku variation { id }") {
    id: ID!
    sku: String
    package: String
    variation: ProductVariation
    dimensions: ProductDimension
    createdBy: User @provides(fields: "totalProductsCreated")
    notes: String @tag(name: "internal")
    research: [ProductResearch!]!
}

type DeprecatedProduct @key(fields: "sku package") {
  sku: String!
  package: String!
  reason: String
  createdBy: User
}

type ProductVariation {
  id: ID!
}

type ProductResearch @key(fields: "study { caseNumber }") {
  study: CaseStudy!
  outcome: String
}

type CaseStudy {
  caseNumber: ID!
  description: String
}

type ProductDimension @shareable {
  size: String
  weight: Float
  unit: String @inaccessible
}

extend type Query {
  product(id: ID!): Product
  deprecatedProduct(sku: String!, package: String!): DeprecatedProduct @deprecated(reason: "Use product query instead")
}

extend type User @key(fields: "email") {
  averageProductsCreatedPerYear: Int @requires(fields: "totalProductsCreated yearsOfEmployment")
  email: ID! @external
  name: String @override(from: "users")
  totalProductsCreated: Int @external
  yearsOfEmployment: Int! @external
}
```

See [apollographql/apollo-federation-subgraph-compatibility](https://github.com/apollographql/apollo-federation-subgraph-compatibility)
for additional details about expected data sets and executed tests.
