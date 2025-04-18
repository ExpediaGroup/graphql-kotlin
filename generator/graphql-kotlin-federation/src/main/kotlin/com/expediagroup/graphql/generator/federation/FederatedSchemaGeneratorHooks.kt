/*
 * Copyright 2025 Expedia, Inc
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

import com.apollographql.federation.graphqljava.printer.ServiceSDLPrinter.generateServiceSDLV2
import com.expediagroup.graphql.generator.TopLevelObject
import com.expediagroup.graphql.generator.annotations.GraphQLName
import com.expediagroup.graphql.generator.directives.DirectiveMetaInformation
import com.expediagroup.graphql.generator.federation.directives.COMPOSE_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.CONTACT_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.CONTACT_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.EXTENDS_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.EXTERNAL_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.EXTERNAL_DIRECTIVE_TYPE
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC_LATEST_URL
import com.expediagroup.graphql.generator.federation.directives.FEDERATION_SPEC_URL_PREFIX
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.INACCESSIBLE_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.INTERFACE_OBJECT_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.KEY_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.LINK_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.LINK_SPEC
import com.expediagroup.graphql.generator.federation.directives.LinkDirective
import com.expediagroup.graphql.generator.federation.directives.LinkImport
import com.expediagroup.graphql.generator.federation.directives.LinkedSpec
import com.expediagroup.graphql.generator.federation.directives.OVERRIDE_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.POLICY_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.PROVIDES_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.REQUIRES_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.REQUIRES_SCOPE_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.SHAREABLE_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.TAG_DIRECTIVE_NAME
import com.expediagroup.graphql.generator.federation.directives.keyDirectiveDefinition
import com.expediagroup.graphql.generator.federation.directives.linkDirectiveDefinition
import com.expediagroup.graphql.generator.federation.directives.overrideDirectiveDefinition
import com.expediagroup.graphql.generator.federation.directives.policyDirectiveDefinition
import com.expediagroup.graphql.generator.federation.directives.providesDirectiveDefinition
import com.expediagroup.graphql.generator.federation.directives.requiresDirectiveDefinition
import com.expediagroup.graphql.generator.federation.directives.requiresScopesDirectiveType
import com.expediagroup.graphql.generator.federation.directives.toAppliedLinkDirective
import com.expediagroup.graphql.generator.federation.directives.toAppliedOverrideDirective
import com.expediagroup.graphql.generator.federation.directives.toAppliedPolicyDirective
import com.expediagroup.graphql.generator.federation.directives.toAppliedRequiresScopesDirective
import com.expediagroup.graphql.generator.federation.exception.DuplicateSpecificationLinkImport
import com.expediagroup.graphql.generator.federation.exception.InvalidFederatedSchema
import com.expediagroup.graphql.generator.federation.exception.UnknownSpecificationException
import com.expediagroup.graphql.generator.federation.execution.EntitiesDataFetcher
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import com.expediagroup.graphql.generator.federation.types.ANY_SCALAR_TYPE
import com.expediagroup.graphql.generator.federation.types.ENTITY_UNION_NAME
import com.expediagroup.graphql.generator.federation.types.FIELD_SET_SCALAR_NAME
import com.expediagroup.graphql.generator.federation.types.FIELD_SET_SCALAR_TYPE
import com.expediagroup.graphql.generator.federation.types.LINK_IMPORT_SCALAR_TYPE
import com.expediagroup.graphql.generator.federation.types.POLICY_SCALAR_TYPE
import com.expediagroup.graphql.generator.federation.types.SCOPE_SCALAR_TYPE
import com.expediagroup.graphql.generator.federation.types.SERVICE_FIELD_DEFINITION
import com.expediagroup.graphql.generator.federation.types._Service
import com.expediagroup.graphql.generator.federation.types.generateEntityFieldDefinition
import com.expediagroup.graphql.generator.federation.validation.FederatedSchemaValidator
import com.expediagroup.graphql.generator.hooks.FlowSubscriptionSchemaGeneratorHooks
import graphql.TypeResolutionEnvironment
import graphql.schema.DataFetcher
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLAppliedDirective
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLNamedType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import java.nio.file.Paths
import kotlin.io.path.name
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation

/**
 * Hooks for generating federated GraphQL schema.
 */
open class FederatedSchemaGeneratorHooks(
    private val resolvers: List<FederatedTypeResolver>
) : FlowSubscriptionSchemaGeneratorHooks() {
    private val validator: FederatedSchemaValidator = FederatedSchemaValidator()

    data class LinkSpec(val namespace: String, val imports: Map<String, String>, val url: String? = FEDERATION_SPEC_LATEST_URL)

    val linkSpecs: MutableMap<String, LinkSpec> = HashMap()

    val federationUrl: String
        get() = linkSpecs[FEDERATION_SPEC]?.url ?: FEDERATION_SPEC_LATEST_URL

    // workaround to https://github.com/ExpediaGroup/graphql-kotlin/issues/1815
    // since those scalars can be renamed, we need to ensure we only generate those scalars just once
    private val fieldSetScalar: GraphQLScalarType by lazy {
        FIELD_SET_SCALAR_TYPE.run {
            val fieldSetScalarName = namespacedTypeName(FEDERATION_SPEC, this.name)
            if (fieldSetScalarName != this.name) {
                return@run this.transform { it.name(fieldSetScalarName) }
            } else {
                this
            }
        }
    }
    private val linkImportScalar: GraphQLScalarType by lazy {
        LINK_IMPORT_SCALAR_TYPE.run {
            val importScalarName = namespacedTypeName(LINK_SPEC, this.name)
            if (importScalarName != this.name) {
                this.transform {
                    it.name(importScalarName)
                }
            } else {
                this
            }
        }
    }
    private val policiesScalar: GraphQLScalarType by lazy {
        POLICY_SCALAR_TYPE.run {
            val policyScalarName = namespacedTypeName(FEDERATION_SPEC, this.name)
            if (policyScalarName != this.name) {
                this.transform {
                    it.name(policyScalarName)
                }
            } else {
                this
            }
        }
    }
    private val scopesScalar: GraphQLScalarType by lazy {
        SCOPE_SCALAR_TYPE.run {
            val scopesScalarName = namespacedTypeName(FEDERATION_SPEC, this.name)
            if (scopesScalarName != this.name) {
                this.transform {
                    it.name(scopesScalarName)
                }
            } else {
                this
            }
        }
    }

    override fun willBuildSchema(
        queries: List<TopLevelObject>,
        mutations: List<TopLevelObject>,
        subscriptions: List<TopLevelObject>,
        additionalTypes: Set<KType>,
        additionalInputTypes: Set<KType>,
        schemaObject: TopLevelObject?
    ): GraphQLSchema.Builder {
        // preprocess any @LinkDirective applications to capture namespaces for all the imported specs
        val appliedLinkDirectives = schemaObject?.kClass?.annotations?.filterIsInstance<LinkDirective>()
        appliedLinkDirectives?.forEach { appliedDirectiveAnnotation ->
            val specUrl = Paths.get(appliedDirectiveAnnotation.url)
            val spec = specUrl.parent.fileName.name

            if (linkSpecs.containsKey(spec)) {
                throw DuplicateSpecificationLinkImport(spec, appliedDirectiveAnnotation.url)
            } else {
                val nameSpace: String = appliedDirectiveAnnotation.`as`.takeIf {
                    it.isNotBlank()
                } ?: spec
                val imports: Map<String, String> = appliedDirectiveAnnotation.import.associate { import ->
                    val importedName = import.`as`.takeIf { it.isNotBlank() } ?: import.name
                    normalizeImportName(import.name) to normalizeImportName(importedName)
                }

                val linkSpec = LinkSpec(nameSpace, imports, appliedDirectiveAnnotation.url)
                linkSpecs[spec] = linkSpec
            }
        }

        // populate defaults
        if (!linkSpecs.containsKey(FEDERATION_SPEC)) {
            linkSpecs[FEDERATION_SPEC] = LinkSpec(
                FEDERATION_SPEC,
                listOf(
                    COMPOSE_DIRECTIVE_NAME,
                    EXTENDS_DIRECTIVE_NAME,
                    EXTERNAL_DIRECTIVE_NAME,
                    INACCESSIBLE_DIRECTIVE_NAME,
                    INTERFACE_OBJECT_DIRECTIVE_NAME,
                    KEY_DIRECTIVE_NAME,
                    OVERRIDE_DIRECTIVE_NAME,
                    PROVIDES_DIRECTIVE_NAME,
                    REQUIRES_DIRECTIVE_NAME,
                    SHAREABLE_DIRECTIVE_NAME,
                    TAG_DIRECTIVE_NAME,
                    FIELD_SET_SCALAR_NAME
                ).associateWith { it }
            )
        }
        if (!linkSpecs.containsKey(LINK_SPEC)) {
            linkSpecs[LINK_SPEC] = LinkSpec(LINK_SPEC, emptyMap())
        }

        return super.willBuildSchema(queries, mutations, subscriptions, additionalTypes, additionalInputTypes, schemaObject)
    }

    private fun normalizeImportName(name: String) = name.replace("@", "")

    /**
     * Add support for FieldSet and LinkImport scalars to the schema.
     */
    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
        FieldSet::class -> fieldSetScalar
        LinkImport::class -> linkImportScalar
        else -> super.willGenerateGraphQLType(type)
    }

    override fun willGenerateDirective(directiveInfo: DirectiveMetaInformation): GraphQLDirective? {
        // Directive requires minimum fed version.
        when (directiveInfo.effectiveName) {
            POLICY_DIRECTIVE_NAME -> checkDirectiveVersionCompatibility(directiveInfo.effectiveName, Pair(2, 6))
            REQUIRES_SCOPE_DIRECTIVE_NAME -> checkDirectiveVersionCompatibility(directiveInfo.effectiveName, Pair(2, 7))
            COMPOSE_DIRECTIVE_NAME -> checkDirectiveVersionCompatibility(directiveInfo.effectiveName, Pair(2, 1))
        }

        return when (directiveInfo.effectiveName) {
            CONTACT_DIRECTIVE_NAME -> CONTACT_DIRECTIVE_TYPE
            EXTERNAL_DIRECTIVE_NAME -> EXTERNAL_DIRECTIVE_TYPE
            KEY_DIRECTIVE_NAME -> keyDirectiveDefinition(fieldSetScalar)
            LINK_DIRECTIVE_NAME -> linkDirectiveDefinition(linkImportScalar)
            POLICY_DIRECTIVE_NAME -> policyDirectiveDefinition(policiesScalar)
            PROVIDES_DIRECTIVE_NAME -> providesDirectiveDefinition(fieldSetScalar)
            REQUIRES_DIRECTIVE_NAME -> requiresDirectiveDefinition(fieldSetScalar)
            REQUIRES_SCOPE_DIRECTIVE_NAME -> requiresScopesDirectiveType(scopesScalar)
            OVERRIDE_DIRECTIVE_NAME -> overrideDirectiveDefinition(federationUrl)
            else -> super.willGenerateDirective(directiveInfo)
        }
    }

    override fun willApplyDirective(directiveInfo: DirectiveMetaInformation, directive: GraphQLDirective): GraphQLAppliedDirective? {
        return when (directiveInfo.effectiveName) {
            REQUIRES_SCOPE_DIRECTIVE_NAME -> {
                directive.toAppliedRequiresScopesDirective(directiveInfo)
            }

            POLICY_DIRECTIVE_NAME -> {
                directive.toAppliedPolicyDirective(directiveInfo)
            }

            OVERRIDE_DIRECTIVE_NAME -> {
                directive.toAppliedOverrideDirective(directiveInfo)
            }

            else -> {
                super.willApplyDirective(directiveInfo, directive)
            }
        }
    }

    override fun didGenerateDirective(directiveInfo: DirectiveMetaInformation, directive: GraphQLDirective): GraphQLDirective {
        // namespace generated directive if needed
        val linkedSpec = directiveInfo.directive.annotationClass.annotations
            .filterIsInstance<LinkedSpec>()
            .map { it.value }
            .firstOrNull()
        if (linkedSpec != null) {
            val finalName = namespacedTypeName(linkedSpec, directive.name)
            if (finalName != directive.name) {
                return directive.transform {
                    it.name(finalName)
                }
            }
        }
        return super.didGenerateDirective(directiveInfo, directive)
    }

    private fun namespacedTypeName(specification: String, name: String): String {
        val spec = linkSpecs[specification] ?: throw UnknownSpecificationException(name, specification)
        return spec.imports[name] ?: "${spec.namespace}__$name"
    }

    override fun didBuildSchema(builder: GraphQLSchema.Builder): GraphQLSchema.Builder {
        val originalSchema = builder.build()
        val originalQuery = originalSchema.queryType

        // apply @link federation spec import only if it was not yet specified
        val federationSpecImportExists = originalSchema.schemaAppliedDirectives.filter { it.name == "link" }.any {
            it.getArgument("url")?.argumentValue?.value?.toString()?.startsWith(FEDERATION_SPEC_URL_PREFIX) == true
        }
        if (!federationSpecImportExists) {
            val fed2Imports = linkSpecs[FEDERATION_SPEC]?.imports
                ?.keys
                ?.mapNotNull {
                    val directive = originalSchema.getDirective(it)
                    if (directive != null) {
                        return@mapNotNull "@${directive.name}"
                    }

                    val scalar = originalSchema.getType(it) as? GraphQLNamedType
                    if (scalar != null) {
                        return@mapNotNull scalar.name
                    }
                    null
                }
                ?: emptyList()
            val linkDirective = linkDirectiveDefinition(linkImportScalar)
            if (!originalSchema.directives.any { it.name == LINK_DIRECTIVE_NAME }) {
                // only add @link directive definition if it doesn't exist yet
                builder.additionalDirective(linkDirective)
            }
            builder.withSchemaAppliedDirective(linkDirective.toAppliedLinkDirective(federationUrl, null, fed2Imports))
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

        // Register the data fetcher for the _service query
        val sdl = getFederatedServiceSdl(builder.build())
        federatedCodeRegistry.dataFetcher(FieldCoordinates.coordinates(originalQuery.name, SERVICE_FIELD_DEFINITION.name), DataFetcher { _Service(sdl) })

        return builder.codeRegistry(federatedCodeRegistry.build())
    }

    /**
     * Federated service may not have any regular queries but will have federated queries. In order to ensure that we
     * have a valid GraphQL schema that can be modified in the [didBuildSchema], query has to have at least one single field.
     *
     * Add federated _service query to ensure it is a valid GraphQL schema.
     */
    override fun didGenerateQueryObject(type: GraphQLObjectType): GraphQLObjectType = GraphQLObjectType.newObject(type)
        .field(SERVICE_FIELD_DEFINITION)
        .build()

    /**
     * Generate SDL that will be returned by _service field
     *
     * See the federation spec for more details:
     * https://www.apollographql.com/docs/federation/subgraph-spec/#enhanced-introspection-with-query_service
     */
    private fun getFederatedServiceSdl(schema: GraphQLSchema): String = generateServiceSDLV2(schema)

    /**
     * Get all the federation entities in the _Entity union, aka all the types with the @key directive.
     *
     * See the federation spec:
     * https://www.apollographql.com/docs/apollo-server/federation/federation-spec/#union-_entity
     */
    private fun getFederatedEntities(originalSchema: GraphQLSchema): Set<String> {
        val keyDirectiveName = namespacedTypeName(FEDERATION_SPEC, KEY_DIRECTIVE_NAME)

        val entities = originalSchema.allTypesAsList
            .filterIsInstance<GraphQLDirectiveContainer>()
            .filter { type -> type.hasAppliedDirective(keyDirectiveName) }

        val errors = entities
            .filterIsInstance<GraphQLNamedType>()
            .map { type -> validator.validateGraphQLType(type) }
            .flatten()
        if (errors.isNotEmpty()) {
            throw InvalidFederatedSchema(errors)
        }

        return entities.filterIsInstance<GraphQLObjectType>()
            .map { it.name }
            .toSet()
    }

    private fun TypeResolutionEnvironment.getObjectName(): String? {
        val kClass = this.getObject<Any>().javaClass.kotlin
        return kClass.findAnnotation<GraphQLName>()?.value
            ?: kClass.simpleName
    }

    private fun checkDirectiveVersionCompatibility(directiveName: String, requiredVersion: Pair<Int, Int>) {
        val federationUrl = linkSpecs[FEDERATION_SPEC]?.url ?: FEDERATION_SPEC_LATEST_URL
        if (!isFederationVersionAtLeast(federationUrl, requiredVersion.first, requiredVersion.second)) {
            throw IllegalArgumentException("@$directiveName directive requires Federation ${requiredVersion.first}.${requiredVersion.second} or later, but version $federationUrl was specified")
        }
    }
}
