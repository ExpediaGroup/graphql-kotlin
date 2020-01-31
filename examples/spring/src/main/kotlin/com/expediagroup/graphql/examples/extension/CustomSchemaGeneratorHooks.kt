/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.examples.extension

import com.expediagroup.graphql.annotations.GraphQLName
import com.expediagroup.graphql.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.examples.model.AdditionalObject
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.GraphQLList
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeReference
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

/**
 * Schema generator hook that adds additional scalar types.
 */
class CustomSchemaGeneratorHooks(override val wiringFactory: KotlinDirectiveWiringFactory) : SchemaGeneratorHooks {

    private val additionalTypes = mutableSetOf<KType>()
    /**
     * Register additional GraphQL types.
     */
    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
        UUID::class -> graphqlUUIDType
        Set::class -> {
            val nestedType = getNestedType(type)
            additionalTypes.add(nestedType)
            GraphQLList.list(GraphQLTypeReference.typeRef(getSimpleName(nestedType.jvmErasure)))
        }
        else -> null
    }

    override fun isValidSuperclass(kClass: KClass<*>): Boolean {
        return if (kClass == Set::class) false else super.isValidSuperclass(kClass)
    }

    override fun onAddAdditionalTypes() = setOf(AdditionalObject::class.starProjectedType)

    private fun getNestedType(type: KType): KType = type.arguments.firstOrNull()?.type ?: throw IllegalArgumentException("Type is not a list")

    private fun getSimpleName(kClass: KClass<*>): String = kClass.findAnnotation<GraphQLName>()?.value
        ?: kClass.simpleName
        ?: throw IllegalArgumentException("Could not get graphql name")
}
    internal val graphqlUUIDType = GraphQLScalarType.newScalar()
        .name("UUID")
        .description("A type representing a formatted java.util.UUID")
        .coercing(UUIDCoercing)
        .build()

    private object UUIDCoercing : Coercing<UUID, String> {
        override fun parseValue(input: Any?): UUID = UUID.fromString(
            serialize(
                input
            )
        )

        override fun parseLiteral(input: Any?): UUID? {
            val uuidString = (input as? StringValue)?.value
            return UUID.fromString(uuidString)
        }

        override fun serialize(dataFetcherResult: Any?): String = dataFetcherResult.toString()
    }
