/*
 * Copyright 2023 Expedia, Inc
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

import com.apollographql.federation.graphqljava.printer.ServiceSDLPrinter.generateServiceSDL
import com.apollographql.federation.graphqljava.printer.ServiceSDLPrinter.generateServiceSDLV2
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.directives.DirectiveMetaInformation
import com.expediagroup.graphql.generator.federation.directives.COMPOSE_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.COMPOSE_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.EXTENDS_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.EXTERNAL_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.EXTERNAL_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.EXTERNAL_DIRECTIVE_TYPE_V2
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC_URL
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.INACCESSIBLE_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.INACCESSIBLE_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.INTERFACE_OBJECT_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.INTERFACE_OBJECT_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.KEY_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.KEY_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.KEY_DIRECTIVE_TYPE_V2
import com.expediagroup.graphql.generator.federation.directives.LINK_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.LINK_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.OVERRIDE_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.OVERRIDE_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.PROVIDES_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.REQUIRES_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.SHAREABLE_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.SHAREABLE_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.TAG_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.appliedLinkDirective
import com.expediagroup.graphql.generator.federation.exception.IncorrectFederatedDirectiveUsage
import com.expediagroup.graphql.generator.federation.execution.EntitiesDataFetcher
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import com.expediagroup.graphql.generator.federation.types.ANY_SCALAR_TYPE
import com.expediagroup.graphql.generator.federation.types.ENTITY_UNION_NAME
import com.expediagroup.graphql.generator.federation.types.FIELD_SET_SCALAR_NAME
import com.expediagroup.graphql.generator.federation.types.FIELD_SET_SCALAR_TYPE
import com.expediagroup.graphql.generator.federation.types.FieldSetTransformer
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
import graphql.schema.SchemaTransformer
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation

/**
 * Hooks for generating federated GraphQL schema.
 */
open class FederatedSchemaGeneratorHooks(
    private val resolvers: List<FederatedTypeResolver>,
    private val optInFederationV2: Boolean = true
) : SchemaGeneratorHooks {
    private val validator = FederatedSchemaValidator()

    private val federationV2OnlyDirectiveNames: Set<String> = setOf(
        COMPOSE_DIRECTIVE_NAME,
        INACCESSIBLE_DIRECTIVE_NAME,
        INTERFACE_OBJECT_DIRECTIVE_NAME,
        LINK_DIRECTIVE_NAME,
        OVERRIDE_DIRECTIVE_NAME,
        SHAREABLE_DIRECTIVE_NAME
    )

    private val federatedDirectiveV1List: List<GraphQLDirective> = listOf(
        EXTENDS_DIRECTIVE_TYPE,
        EXTERNAL_DIRECTIVE_TYPE,
        KEY_DIRECTIVE_TYPE,
        PROVIDES_DIRECTIVE_TYPE,
        REQUIRES_DIRECTIVE_TYPE
    )
    private val federatedDirectiveV2List: List<GraphQLDirective> = listOf(
        COMPOSE_DIRECTIVE_TYPE,
        EXTENDS_DIRECTIVE_TYPE,
        EXTERNAL_DIRECTIVE_TYPE_V2,
        INACCESSIBLE_DIRECTIVE_TYPE,
        INTERFACE_OBJECT_DIRECTIVE_TYPE,
        KEY_DIRECTIVE_TYPE_V2,
        LINK_DIRECTIVE_TYPE,
        OVERRIDE_DIRECTIVE_TYPE,
        PROVIDES_DIRECTIVE_TYPE,
        REQUIRES_DIRECTIVE_TYPE,
        SHAREABLE_DIRECTIVE_TYPE,
        TAG_DIRECTIVE_TYPE
    )

    /**
     * Add support for _FieldSet scalar to the schema.
     */
    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
        FieldSet::class -> FIELD_SET_SCALAR_TYPE
        else -> super.willGenerateGraphQLType(type)
    }

    override fun willGenerateDirective(directiveInfo: DirectiveMetaInformation): GraphQLDirective? =
        if (optInFederationV2) {
            willGenerateFederatedDirectiveV2(directiveInfo)
        } else {
            willGenerateFederatedDirective(directiveInfo)
        }

    private fun willGenerateFederatedDirective(directiveInfo: DirectiveMetaInformation): GraphQLDirective? = when {
        federationV2OnlyDirectiveNames.contains(directiveInfo.effectiveName) -> throw IncorrectFederatedDirectiveUsage(directiveInfo.effectiveName)
        EXTERNAL_DIRECTIVE_NAME == directiveInfo.effectiveName -> EXTERNAL_DIRECTIVE_TYPE
        KEY_DIRECTIVE_NAME == directiveInfo.effectiveName -> KEY_DIRECTIVE_TYPE
        else -> super.willGenerateDirective(directiveInfo)
    }

    private fun willGenerateFederatedDirectiveV2(directiveInfo: DirectiveMetaInformation): GraphQLDirective? = when (directiveInfo.effectiveName) {
        EXTERNAL_DIRECTIVE_NAME -> EXTERNAL_DIRECTIVE_TYPE_V2
        KEY_DIRECTIVE_NAME -> KEY_DIRECTIVE_TYPE_V2
        LINK_DIRECTIVE_NAME -> LINK_DIRECTIVE_TYPE
        else -> super.willGenerateDirective(directiveInfo)
    }

    override fun didGenerateGraphQLType(type: KType, generatedType: GraphQLType): GraphQLType {
        validator.validateGraphQLType(generatedType)
        return super.didGenerateGraphQLType(type, generatedType)
    }

    override fun willBuildSchema(builder: GraphQLSchema.Builder): GraphQLSchema.Builder {
        val originalSchema = builder.build()
        val originalQuery = originalSchema.queryType

        findMissingFederationDirectives(originalSchema.directives).forEach {
            builder.additionalDirective(it)
        }
        if (optInFederationV2) {
            val fed2Imports = federatedDirectiveV2List.map { "@${it.name}" }
                .minus("@$LINK_DIRECTIVE_NAME")
                .plus(FIELD_SET_SCALAR_NAME)

            builder.withSchemaDirective(LINK_DIRECTIVE_TYPE)
                .withSchemaAppliedDirective(appliedLinkDirective(FEDERATION_SPEC_URL, fed2Imports))
        }

        val federatedCodeRegistry = GraphQLCodeRegistry.newCodeRegistry(originalSchema.codeRegistry)
        val entityTypeNames = getFederatedEntities(originalSchema)
        // Add the _entities field to the query and register all the _Entity union types
        if (entityTypeNames.isNotEmpty()) {
            val federatedQuery = GraphQLObjectType.newObject(originalQuery)

            val entityField = generateEntityFieldDefinition(entityTypeNames)
            federatedQuery.field(entityField)

            federatedCodeRegistry.dataFetcher(FieldCoordinates.coordinates(originalQuery.name, entityField.name), EntitiesDataFetcher(resolvers))
            federatedCodeRegistry.typeResolver(ENTITY_UNION_NAME) { env: TypeResolutionEnvironment -> env.schema.getObjectType(env.getObjectName()) }

            builder.query(federatedQuery)
                .codeRegistry(federatedCodeRegistry.build())
                .additionalType(ANY_SCALAR_TYPE)
        }

        val federatedBuilder = if (optInFederationV2) {
            builder
        } else {
            // transform schema to rename FieldSet to _FieldSet
            GraphQLSchema.newSchema(SchemaTransformer.transformSchema(builder.build(), FieldSetTransformer()))
        }

        // Register the data fetcher for the _service query
        val sdl = getFederatedServiceSdl(federatedBuilder.build())
        federatedCodeRegistry.dataFetcher(FieldCoordinates.coordinates(originalQuery.name, SERVICE_FIELD_DEFINITION.name), DataFetcher { _Service(sdl) })

        return federatedBuilder.codeRegistry(federatedCodeRegistry.build())
    }

    private fun findMissingFederationDirectives(existingDirectives: List<GraphQLDirective>): List<GraphQLDirective> {
        val existingDirectiveNames = existingDirectives.map { it.name }
        return federatedDirectiveList().filter {
            !existingDirectiveNames.contains(it.name)
        }
    }

    private fun federatedDirectiveList(): List<GraphQLDirective> = if (optInFederationV2) {
        federatedDirectiveV2List
    } else {
        federatedDirectiveV1List
    }

    /**
     * Federated service may not have any regular queries but will have federated queries. In order to ensure that we
     * have a valid GraphQL schema that can be modified in the [willBuildSchema], query has to have at least one single field.
     *
     * Add federated _service query to ensure it is a valid GraphQL schema.
     */
    override fun didGenerateQueryObject(type: GraphQLObjectType): GraphQLObjectType = GraphQLObjectType.newObject(type)
        .field(SERVICE_FIELD_DEFINITION)
        .also {
            if (!optInFederationV2) {
                it.withAppliedDirective(EXTENDS_DIRECTIVE_TYPE.toAppliedDirective())
            }
        }
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
        return if (optInFederationV2) {
            generateServiceSDLV2(schema)
        } else {
            generateServiceSDL(schema, false)
        }
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
            .filter { type -> type.hasAppliedDirective(KEY_DIRECTIVE_NAME) }
            .map { it.name }
            .toSet()
    }

    private fun TypeResolutionEnvironment.getObjectName(): String? {
        val kClass = this.getObject<Any>().javaClass.kotlin
        return kClass.findAnnotation<GraphQLName>()?.value
            ?: kClass.simpleName
    }
}
