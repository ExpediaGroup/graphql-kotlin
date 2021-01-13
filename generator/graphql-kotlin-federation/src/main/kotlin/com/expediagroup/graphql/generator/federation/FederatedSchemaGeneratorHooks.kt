/*
 * Copyright 2021 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.generator.federation

import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.directives.DEPRECATED_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.extensions.print
import com.expediagroup.graphql.generator.federation.directives.EXTENDS_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.EXTERNAL_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.KEY_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.KEY_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.PROVIDES_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.REQUIRES_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.execution.EntityResolver
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import com.expediagroup.graphql.generator.federation.extensions.addDirectivesIfNotPresent
import com.expediagroup.graphql.generator.federation.types.ANY_SCALAR_TYPE
import com.expediagroup.graphql.generator.federation.types.ENTITY_UNION_NAME
import com.expediagroup.graphql.generator.federation.types.FIELD_SET_SCALAR_TYPE
import com.expediagroup.graphql.generator.federation.types.SERVICE_FIELD_DEFINITION
import com.expediagroup.graphql.generator.federation.types._Service
import com.expediagroup.graphql.generator.federation.types.generateEntityFieldDefinition
import com.expediagroup.graphql.generator.federation.validation.FederatedSchemaValidator
import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import graphql.TypeResolutionEnvironment
import graphql.schema.DataFetcher
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import java.util.function.Predicate
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation

/**
 * Hooks for generating federated GraphQL schema.
 */
open class FederatedSchemaGeneratorHooks(private val resolvers: List<FederatedTypeResolver<*>>) : SchemaGeneratorHooks {
    private val scalarDefinitionRegex = "(^\".+\"$[\\r\\n])?^scalar (_FieldSet|_Any)$[\\r\\n]*".toRegex(setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
    private val emptyQueryRegex = "^type Query @extends \\s*\\{\\s*}\\s*".toRegex(setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
    private val serviceFieldRegex = "\\s*_service: _Service".toRegex(setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
    private val serviceTypeRegex = "^type _Service\\s*\\{\\s*sdl: String!\\s*}\\s*".toRegex(setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
    private val validator = FederatedSchemaValidator()

    private val federatedDirectiveTypes: List<GraphQLDirective> = listOf(EXTERNAL_DIRECTIVE_TYPE, REQUIRES_DIRECTIVE_TYPE, PROVIDES_DIRECTIVE_TYPE, KEY_DIRECTIVE_TYPE, EXTENDS_DIRECTIVE_TYPE)
    private val directivesToInclude: List<String> = federatedDirectiveTypes.map { it.name }.plus(DEPRECATED_DIRECTIVE_NAME)
    private val customDirectivePredicate: Predicate<GraphQLDirective> = Predicate { directivesToInclude.contains(it.name) }

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

        // Add all the federation directives if they are not present
        val federatedSchemaBuilder = originalSchema.addDirectivesIfNotPresent(federatedDirectiveTypes)

        // Register the data fetcher for the _service query
        val sdl = getFederatedServiceSdl(originalSchema)
        federatedCodeRegistry.dataFetcher(FieldCoordinates.coordinates(originalQuery.name, SERVICE_FIELD_DEFINITION.name), DataFetcher { _Service(sdl) })

        // Add the _entities field to the query and register all the _Entity union types
        val federatedQuery = GraphQLObjectType.newObject(originalQuery)
        val entityTypeNames = getFederatedEntities(originalSchema)
        if (entityTypeNames.isNotEmpty()) {
            val entityField = generateEntityFieldDefinition(entityTypeNames)
            federatedQuery.field(entityField)

            federatedCodeRegistry.dataFetcher(FieldCoordinates.coordinates(originalQuery.name, entityField.name), EntityResolver(resolvers))
            federatedCodeRegistry.typeResolver(ENTITY_UNION_NAME) { env: TypeResolutionEnvironment -> env.schema.getObjectType(env.getObjectName()) }
            federatedSchemaBuilder.additionalType(ANY_SCALAR_TYPE)
        }

        return federatedSchemaBuilder.query(federatedQuery.build())
            .codeRegistry(federatedCodeRegistry.build())
    }

    /**
     * Federated service may not have any regular queries but will have federated queries. In order to ensure that we
     * have a valid GraphQL schema that can be modified in the [willBuildSchema], query has to have at least one single field.
     *
     * Add federated _service query to ensure it is a valid GraphQL schema.
     */
    override fun didGenerateQueryObject(query: GraphQLObjectType): GraphQLObjectType = GraphQLObjectType.newObject(query)
        .field(SERVICE_FIELD_DEFINITION)
        .withDirective(EXTENDS_DIRECTIVE_TYPE)
        .build()

    /**
     * Get the modified SDL returned by _service field
     *
     * It should NOT contain:
     *   - default schema definition
     *   - empty Query type
     *   - any directive definitions
     *   - any custom directives
     *   - new federated scalars
     *
     * See the federation spec for more details:
     * https://www.apollographql.com/docs/apollo-server/federation/federation-spec/#query_service
     */
    private fun getFederatedServiceSdl(schema: GraphQLSchema): String {
        return schema.print(
            includeDefaultSchemaDefinition = false,
            includeDirectiveDefinitions = false,
            includeDirectivesFilter = customDirectivePredicate
        ).replace(scalarDefinitionRegex, "")
            .replace(serviceFieldRegex, "")
            .replace(serviceTypeRegex, "")
            .replace(emptyQueryRegex, "")
            .trim()
    }

    /**
     * Get all the federation entities in the _Entity union, aka all the types with the @key directive.
     *
     * See the federation spec:
     * https://www.apollographql.com/docs/apollo-server/federation/federation-spec/#union-_entity
     */
    private fun getFederatedEntities(originalSchema: GraphQLSchema): Set<String> {
        return originalSchema.allTypesAsList
            .asSequence()
            .filterIsInstance<GraphQLObjectType>()
            .filter { type -> type.getDirective(KEY_DIRECTIVE_NAME) != null }
            .map { it.name }
            .toSet()
    }

    private fun TypeResolutionEnvironment.getObjectName(): String? {
        val kClass = this.getObject<Any>().javaClass.kotlin
        return kClass.findAnnotation<GraphQLName>()?.value
            ?: kClass.simpleName
    }
}
