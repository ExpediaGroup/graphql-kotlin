package com.expediagroup.graphql.plugin.client.generator.exceptions

/**
 * Exception thrown when specified schema file path is not found or unavailable
 */
internal class SchemaUnavailableException(schemaPath: String) : RuntimeException("Specified schema file=$schemaPath does not exist")
