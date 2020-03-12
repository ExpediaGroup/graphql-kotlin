package com.expediagroup.graphql.plugin.generator

import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry

private val testSchemaSDL = """
    type Query {
      enumTestQuery: MyCustomEnum
      objectTestQuery: MyCustomObject
      inputObjectTestQuery(criteria: TestCriteriaInput): Boolean
    }

    "Custom enum description"
    enum MyCustomEnum {
      "First enum value"
      ONE,
      "Second enum value"
      TWO
    }

    "Custom type description"
    type MyCustomObject {
      "Some unique identifier"
      id: Int!,
      "Some object name"
      name: String!,
      "Optional value"
      optional: String,
      "Some additional details"
      details: MyDetailsObject
    }

    "Inner type object description"
    type MyDetailsObject {
      "Unique identifier"
      id: Int!,
      "Boolean flag"
      flag: Boolean!,
      "Actual detail value"
      value: String!
    }

    "Test input object"
    input TestCriteriaInput {
      "Minimum value for test criteria"
      min: Float,
      "Maximum value for test criteria"
      max: Float
    }
""".trimIndent()

internal val testSchema: TypeDefinitionRegistry = SchemaParser().parse(testSchemaSDL)
