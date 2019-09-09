/*
 * Copyright 2019 Expedia Group, Inc.
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
import com.expediagroup.graphql.extensions.print
import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.directives.extendsDirectiveType
import com.expediagroup.graphql.federation.execution.EntityResolver
import com.expediagroup.graphql.federation.execution.FederatedTypeRegistry
import com.expediagroup.graphql.federation.types.ANY_SCALAR_TYPE
import com.expediagroup.graphql.federation.types.FIELD_SET_SCALAR_TYPE
import com.expediagroup.graphql.federation.types.SERVICE_FIELD_DEFINITION
import com.expediagroup.graphql.federation.types._Service
import com.expediagroup.graphql.federation.types.generateEntityFieldDefinition
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
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
