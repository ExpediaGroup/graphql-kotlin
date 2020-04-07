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

import com.apollographql.federation.graphqljava.Federation
import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.execution.EntityResolver
import com.expediagroup.graphql.federation.execution.FederatedTypeRegistry
import com.expediagroup.graphql.federation.extensions.getObjectName
import com.expediagroup.graphql.federation.types.FIELD_SET_SCALAR_TYPE
import com.expediagroup.graphql.federation.validation.FederatedSchemaValidator
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLType
import kotlin.reflect.KType

/**
 * Hooks for generating federated GraphQL schema.
 */
open class FederatedSchemaGeneratorHooks(private val federatedTypeRegistry: FederatedTypeRegistry) : SchemaGeneratorHooks {

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

        return GraphQLSchema.newSchema(
            Federation.transform(originalSchema)
                .resolveEntityType { env -> env.schema.getObjectType(env.getObjectName()) }
                .fetchEntities(EntityResolver(federatedTypeRegistry))
                .build()
        )
    }

    // skip validation for empty query type - federation will add _service query
    override fun didGenerateQueryObject(type: GraphQLObjectType): GraphQLObjectType = type
}
