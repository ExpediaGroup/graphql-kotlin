/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.federation

import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.directives.DEPRECATED_DIRECTIVE_NAME
import com.expediagroup.graphql.extensions.print
import com.expediagroup.graphql.federation.directives.EXTENDS_DIRECTIVE_TYPE
import com.expediagroup.graphql.federation.directives.EXTERNAL_DIRECTIVE_TYPE
import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.directives.KEY_DIRECTIVE_NAME
import com.expediagroup.graphql.federation.directives.KEY_DIRECTIVE_TYPE
import com.expediagroup.graphql.federation.directives.PROVIDES_DIRECTIVE_TYPE
import com.expediagroup.graphql.federation.directives.REQUIRES_DIRECTIVE_TYPE
import com.expediagroup.graphql.federation.execution.EntityResolver
import com.expediagroup.graphql.federation.execution.FederatedTypeRegistry
import com.expediagroup.graphql.federation.extensions.addDirectivesIfNotPresent
import com.expediagroup.graphql.federation.types.ANY_SCALAR_TYPE
import com.expediagroup.graphql.federation.types.FIELD_SET_SCALAR_TYPE
import com.expediagroup.graphql.federation.types.SERVICE_FIELD_DEFINITION
import com.expediagroup.graphql.federation.types._Service
import com.expediagroup.graphql.federation.types.generateEntityFieldDefinition
import com.expediagroup.graphql.federation.validation.FederatedSchemaValidator
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
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
open class FederatedSchemaGeneratorHooks(private val federatedTypeRegistry: FederatedTypeRegistry) : SchemaGeneratorHooks {
    private val scalarDefinitionRegex = "(^\".+\"$[\\r\\n])?^scalar (_FieldSet|_Any)$[\\r\\n]*".toRegex(setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
    private val emptyQueryRegex = "^type Query(?!\\s*\\{)\\s+".toRegex(setOf(RegexOption.MULTILINE, RegexOption.IGNORE_CASE))
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

        // Modify the query type to have the service field and extends directive
        val federatedQuery = GraphQLObjectType.newObject(originalQuery)
            .field(SERVICE_FIELD_DEFINITION)
            .withDirective(EXTENDS_DIRECTIVE_TYPE)

        /**
         * Register the data fetcher for the SDL returned by _service field.
         *
         * It should NOT contain:
         *   - default schema definition
         *   - empty Query type
         *   - any directive definitions
         *   - any custom directives
         *   - new federated scalars
         */
        val sdl = originalSchema.print(
            includeDefaultSchemaDefinition = false,
            includeDirectiveDefinitions = false,
            includeDirectivesFilter = customDirectivePredicate
        ).replace(scalarDefinitionRegex, "")
            .replace(emptyQueryRegex, "")
            .trim()
        federatedCodeRegistry.dataFetcher(FieldCoordinates.coordinates(originalQuery.name, SERVICE_FIELD_DEFINITION.name), DataFetcher { _Service(sdl) })

        val entityTypeNames = originalSchema.allTypesAsList
            .asSequence()
            .filterIsInstance<GraphQLObjectType>()
            .filter { type -> type.getDirective(KEY_DIRECTIVE_NAME) != null }
            .map { it.name }
            .toSet()

        // Add the _entities field to the query
        if (entityTypeNames.isNotEmpty()) {
            val entityField = generateEntityFieldDefinition(entityTypeNames)
            federatedQuery.field(entityField)

            federatedCodeRegistry.dataFetcher(FieldCoordinates.coordinates(originalQuery.name, entityField.name), EntityResolver(federatedTypeRegistry))
            federatedCodeRegistry.typeResolver("_Entity") { env: TypeResolutionEnvironment -> env.schema.getObjectType(env.getObjectName()) }
            federatedSchemaBuilder.additionalType(ANY_SCALAR_TYPE)
        }

        return federatedSchemaBuilder.query(federatedQuery.build())
            .codeRegistry(federatedCodeRegistry.build())
    }

    // skip validation for empty query type - federation will add _service query
    override fun didGenerateQueryObject(type: GraphQLObjectType): GraphQLObjectType = type
}

private fun TypeResolutionEnvironment.getObjectName(): String? {
    val kClass = this.getObject<Any>().javaClass.kotlin
    return kClass.findAnnotation<GraphQLName>()?.value
        ?: kClass.simpleName
}
