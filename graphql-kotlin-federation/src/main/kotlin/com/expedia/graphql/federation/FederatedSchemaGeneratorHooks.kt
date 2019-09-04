package com.expedia.graphql.federation

import com.expedia.graphql.annotations.GraphQLName
import com.expedia.graphql.extensions.print
import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.extendsDirectiveType
import com.expedia.graphql.federation.execution.EntityResolver
import com.expedia.graphql.federation.execution.FederatedTypeRegistry
import com.expedia.graphql.federation.types.ANY_SCALAR_TYPE
import com.expedia.graphql.federation.types.FIELD_SET_SCALAR_TYPE
import com.expedia.graphql.federation.types.SERVICE_FIELD_DEFINITION
import com.expedia.graphql.federation.types._Service
import com.expedia.graphql.federation.types.generateEntityFieldDefinition
import com.expedia.graphql.hooks.SchemaGeneratorHooks
import graphql.TypeResolutionEnvironment
import graphql.schema.DataFetcher
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation

/**
 * Hooks for generating federated GraphQL schema.
 */
open class FederatedSchemaGeneratorHooks(private val federatedTypeRegistry: FederatedTypeRegistry) : SchemaGeneratorHooks {
    private val directiveRegex = "(^#.+$[\\r\\n])?^directive @\\w+.+$[\\r\\n]*".toRegex(setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
    private val scalarRegex = "(^#.+$[\\r\\n])?^scalar (_FieldSet|_Any)$[\\r\\n]*".toRegex(setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
    private val emptyQuery = "^type Query \\{$\\s+^\\}$\\s+".toRegex(setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
    private val validator = FederatedSchemaValidator()

    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
        FieldSet::class -> FIELD_SET_SCALAR_TYPE
        else -> null
    }

    override fun didGenerateGraphQLType(type: KType, generatedType: GraphQLType): GraphQLType {
        validator.validateGraphQLType(generatedType)
        return super.didGenerateGraphQLType(type, generatedType)
    }

    override fun willBuildSchema(builder: GraphQLSchema.Builder): GraphQLSchema.Builder {
        val originalSchema = builder.build()
        val originalQuery = originalSchema.queryType

        val federatedCodeRegistry = GraphQLCodeRegistry.newCodeRegistry(originalSchema.codeRegistry)
        val federatedSchema = GraphQLSchema.newSchema(originalSchema)
        val federatedQuery = GraphQLObjectType.newObject(originalQuery)
            .field(SERVICE_FIELD_DEFINITION)
            .withDirective(extendsDirectiveType)

        val entityTypeNames = originalSchema.allTypesAsList
            .asSequence()
            .filterIsInstance<GraphQLObjectType>()
            .filter { type -> type.getDirective("key") != null }
            .map { it.name }
            .toSet()

        // register new federated queries
        if (entityTypeNames.isNotEmpty()) {
            val entityField = generateEntityFieldDefinition(entityTypeNames)
            federatedQuery.field(entityField)

            // SDL returned by _service query should NOT contain
            // - default schema definition
            // - empty Query type
            // - directives
            // - new scalars
            val sdl = originalSchema.print(includeDefaultSchemaDefinition = false)
                .replace(directiveRegex, "")
                .replace(scalarRegex, "")
                .replace(emptyQuery, "")
                .trim()

            federatedCodeRegistry.dataFetcher(FieldCoordinates.coordinates(originalQuery.name, SERVICE_FIELD_DEFINITION.name), DataFetcher { _Service(sdl) })
            federatedCodeRegistry.dataFetcher(FieldCoordinates.coordinates(originalQuery.name, entityField.name), EntityResolver(federatedTypeRegistry))
            federatedCodeRegistry.typeResolver("_Entity") { env: TypeResolutionEnvironment -> env.schema.getObjectType(env.getObjectName()) }
            federatedSchema.additionalType(ANY_SCALAR_TYPE)
        }

        return federatedSchema.query(federatedQuery.build())
            .codeRegistry(federatedCodeRegistry.build())
    }
}

private fun TypeResolutionEnvironment.getObjectName(): String? {
    val kClass = this.getObject<Any>().javaClass.kotlin
    return kClass.findAnnotation<GraphQLName>()?.value
        ?: kClass.simpleName
}
