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

package com.expediagroup.graphql.plugin.client.generator

import com.expediagroup.graphql.plugin.client.generator.exceptions.MultipleOperationsInFileException
import com.expediagroup.graphql.plugin.client.generator.types.generateGraphQLObjectTypeSpec
import com.expediagroup.graphql.plugin.client.generator.types.generateVariableTypeSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.ObjectTypeDefinition
import graphql.language.OperationDefinition
import graphql.parser.Parser
import graphql.schema.idl.TypeDefinitionRegistry
import java.io.File

private const val CORE_TYPES_PACKAGE = "com.expediagroup.graphql.client.types"

/**
 * GraphQL client code generator that uses [KotlinPoet](https://github.com/square/kotlinpoet) to generate Kotlin classes based on the specified GraphQL queries.
 */
class GraphQLClientGenerator(
    private val graphQLSchema: TypeDefinitionRegistry,
    private val config: GraphQLClientGeneratorConfig
) {
    private val documentParser: Parser = Parser()
    private val typeAliases: MutableMap<String, TypeAliasSpec> = mutableMapOf()

    /**
     * Generate GraphQL clients for the specified queries.
     */
    fun generate(queries: List<File>): List<FileSpec> {
        val result = mutableListOf<FileSpec>()
        for (query in queries) {
            result.add(generate(query))
        }
        if (typeAliases.isNotEmpty()) {
            val typeAliasSpec = FileSpec.builder(packageName = config.packageName, fileName = "GraphQLTypeAliases")
            typeAliases.forEach { (_, alias) ->
                typeAliasSpec.addTypeAlias(alias)
            }
            result.add(typeAliasSpec.build())
        }
        return result
    }

    /**
     * Generate GraphQL client wrapper class and data classes that match the specified query.
     */
    internal fun generate(queryFile: File): FileSpec {
        val queryConst = queryFile.readText().trim()
        val queryDocument = documentParser.parseDocument(queryConst)

        val operationDefinitions = queryDocument.definitions.filterIsInstance(OperationDefinition::class.java)
        if (operationDefinitions.size > 1) {
            throw MultipleOperationsInFileException
        }

        val fileSpec = FileSpec.builder(packageName = config.packageName, fileName = queryFile.nameWithoutExtension.capitalize())

        operationDefinitions.forEach { operationDefinition ->
            val operationTypeName = operationDefinition.name?.capitalize() ?: queryFile.nameWithoutExtension.capitalize()
            val context = GraphQLClientGeneratorContext(
                packageName = config.packageName,
                graphQLSchema = graphQLSchema,
                rootType = operationTypeName,
                queryDocument = queryDocument,
                allowDeprecated = config.allowDeprecated,
                customScalarMap = config.customScalarMap
            )
            val queryConstName = operationTypeName.toUpperUnderscore()
            val queryConstProp = PropertySpec.builder(queryConstName, STRING)
                .addModifiers(KModifier.CONST)
                .initializer("%S", queryConst)
                .build()
            fileSpec.addProperty(queryConstProp)

            val rootType = findRootType(operationDefinition)
            val graphQLResponseTypeSpec = generateGraphQLObjectTypeSpec(context, rootType, operationDefinition.selectionSet, "Result")
            val kotlinResultTypeName = ClassName(context.packageName, "${context.rootType}.${graphQLResponseTypeSpec.name}")

            val queryProperty = PropertySpec.builder("query", STRING, KModifier.OVERRIDE)
                .initializer("%N", queryConstProp)
                .build()
            val operationTypeSpec = TypeSpec.classBuilder(operationTypeName)
                .addSuperinterface(ClassName(CORE_TYPES_PACKAGE, "GraphQLClientRequest").parameterizedBy(kotlinResultTypeName))
                .addProperty(queryProperty)

            if (operationDefinition.name != null) {
                val operationNameProperty = PropertySpec.builder("operationName", STRING, KModifier.OVERRIDE)
                    .initializer("%S", operationDefinition.name)
                    .build()
                operationTypeSpec.addProperty(operationNameProperty)
            }

            val variableType: TypeSpec? = generateVariableTypeSpec(context, operationDefinition.variableDefinitions)
            if (variableType != null) {
                operationTypeSpec.addType(variableType)

                val variablesClassName = ClassName(config.packageName, "$operationTypeName.Variables")
                val variablesProperty = PropertySpec.builder("variables", variablesClassName, KModifier.OVERRIDE)
                    .initializer("variables")
                    .build()
                operationTypeSpec.addProperty(variablesProperty)

                val constructor = FunSpec.constructorBuilder()
                    .addParameter("variables", variablesClassName, KModifier.OVERRIDE)
                    .build()
                operationTypeSpec.primaryConstructor(constructor)
            }

            val parameterizedReturnType = ClassName("kotlin.reflect", "KClass").parameterizedBy(kotlinResultTypeName)
            operationTypeSpec.addFunction(
                FunSpec.builder("responseType")
                    .addModifiers(KModifier.OVERRIDE)
                    .returns(parameterizedReturnType)
                    .addStatement("return %T::class", kotlinResultTypeName)
                    .build()
            )

            context.typeSpecs.forEach {
                operationTypeSpec.addType(it.value)
            }
            fileSpec.addType(operationTypeSpec.build())

            typeAliases.putAll(context.typeAliases)
        }
        return fileSpec.build()
    }

    private fun findRootType(operationDefinition: OperationDefinition): ObjectTypeDefinition {
        val operationNames = if (graphQLSchema.schemaDefinition().isPresent) {
            graphQLSchema.schemaDefinition().get().operationTypeDefinitions.associateBy({ it.name.toUpperCase() }, { it.typeName.name })
        } else {
            mapOf(
                OperationDefinition.Operation.QUERY.name to "Query",
                OperationDefinition.Operation.MUTATION.name to "Mutation",
                OperationDefinition.Operation.SUBSCRIPTION.name to "Subscription"
            )
        }
        val rootType = operationNames[operationDefinition.operation.name]
        return graphQLSchema.getType(rootType).get() as ObjectTypeDefinition
    }
}

internal fun String.toUpperUnderscore(): String {
    val builder = StringBuilder()
    val nameCharArray = this.toCharArray()
    for ((index, c) in nameCharArray.withIndex()) {
        if (c.isUpperCase() && index > 0) {
            if (nameCharArray[index - 1].isLowerCase() || (index < nameCharArray.size - 1 && nameCharArray[index + 1].isLowerCase())) {
                builder.append("_")
            }
        }
        builder.append(c.toUpperCase())
    }
    return builder.toString()
}
