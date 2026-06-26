package com.expediagroup.graphql.generator.internal.types.fixtures

import graphql.Scalars
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter

internal fun GraphQLArgument.toSdl(fieldName: String): String =
    SchemaPrinter().print(
        GraphQLSchema.newSchema()
            .query(
                GraphQLObjectType.newObject()
                    .name("Query")
                    .field(
                        GraphQLFieldDefinition.newFieldDefinition()
                            .name(fieldName)
                            .type(Scalars.GraphQLString)
                            .argument(this)
                    )
                    .build()
            )
            .build()
    )
