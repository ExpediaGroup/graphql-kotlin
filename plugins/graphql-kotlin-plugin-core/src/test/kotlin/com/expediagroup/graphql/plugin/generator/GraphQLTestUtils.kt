package com.expediagroup.graphql.plugin.generator

import com.expediagroup.graphql.client.converters.CustomScalarConverter
import graphql.language.Document
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import io.mockk.mockk
import java.util.UUID

internal fun mockContext(
    packageName: String = "com.expediagroup.graphql.plugin.generator.types.test",
    graphQLSchema: TypeDefinitionRegistry = testSchema(),
    rootType: String = "JunitTestQueryResult",
    queryDocument: Document = mockk(),
    allowDeprecated: Boolean = false,
    scalarTypeToConverterMapping: Map<String, CustomScalarConverterMapping> = emptyMap()
): GraphQLClientGeneratorContext = GraphQLClientGeneratorContext(
    packageName = packageName,
    graphQLSchema = graphQLSchema,
    rootType = rootType,
    queryDocument = queryDocument,
    allowDeprecated = allowDeprecated,
    scalarTypeToConverterMapping = scalarTypeToConverterMapping)

internal fun testSchema(): TypeDefinitionRegistry {
    val schemaFileStream = ClassLoader.getSystemClassLoader().getResourceAsStream("testSchema.graphql") ?: throw RuntimeException("unable to locate test schema")
    return schemaFileStream.use {
        SchemaParser().parse(schemaFileStream)
    }
}

internal class UUIDConverter : CustomScalarConverter<UUID> {
    override fun toScalar(rawValue: String): UUID = UUID.fromString(rawValue)
    override fun toJson(value: UUID): String = value.toString()
}
