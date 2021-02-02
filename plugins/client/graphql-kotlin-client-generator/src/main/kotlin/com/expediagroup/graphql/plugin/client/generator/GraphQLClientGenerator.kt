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
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import graphql.language.ObjectTypeDefinition
import graphql.language.OperationDefinition
import graphql.parser.Parser
import graphql.schema.idl.TypeDefinitionRegistry
import io.ktor.client.request.HttpRequestBuilder
import java.io.File

private const val LIBRARY_CLIENT_PACKAGE = "com.expediagroup.graphql.client"
private const val LIBRARY_CLIENT_KTOR_PACKAGE = "com.expediagroup.graphql.client.ktor"
private const val LIBRARY_CLIENT_SPRING_PACKAGE = "com.expediagroup.graphql.client.spring"
private const val CORE_TYPES_PACKAGE = "com.expediagroup.graphql.types"

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
        val queryConst = queryFile.readText()
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

            val variableType: TypeSpec? = generateVariableTypeSpec(context, operationDefinition.variableDefinitions)

            val rootType = findRootType(operationDefinition)
            val graphQLResponseTypeSpec = generateGraphQLObjectTypeSpec(context, rootType, operationDefinition.selectionSet, "Result")
            val kotlinResultTypeName = ClassName(context.packageName, "${context.rootType}.${graphQLResponseTypeSpec.name}")

            val operationTypeSpec = TypeSpec.classBuilder(operationTypeName)
            operationTypeSpec.superclass(ClassName(LIBRARY_CLIENT_PACKAGE, "GraphQLClientRequest"))
            operationTypeSpec.addSuperclassConstructorParameter("%N", queryConstProp)
            if (operationDefinition.name != null) {
                operationTypeSpec.addSuperclassConstructorParameter("%S", operationDefinition.name)
            }

            if (variableType != null) {
                operationTypeSpec.addType(variableType)

                val constructor = FunSpec.constructorBuilder()
                    .addParameter("variables", ClassName(config.packageName, "$operationTypeName.Variables"))
                    .build()
                operationTypeSpec.primaryConstructor(constructor)
                operationTypeSpec.addSuperclassConstructorParameter("%L", "variables")
            }

            val parameterizedReturnType = ClassName("java.lang", "Class").parameterizedBy(kotlinResultTypeName)
            operationTypeSpec.addFunction(
                FunSpec.builder("responseType")
                    .addModifiers(KModifier.OVERRIDE)
                    .returns(parameterizedReturnType)
                    .addStatement("return %T::class.java", kotlinResultTypeName)
                    .build()
            )

            context.typeSpecs.forEach {
                operationTypeSpec.addType(it.value)
            }
            fileSpec.addType(operationTypeSpec.build())

            val clientExtensionFunction = FunSpec.builder("execute$operationTypeName")
                .addParameter("request", ClassName(config.packageName, operationTypeName))
                .returns(ClassName(CORE_TYPES_PACKAGE, "GraphQLResponse").parameterizedBy(kotlinResultTypeName))
                .addModifiers(KModifier.SUSPEND)

            when (config.clientType) {
                GraphQLClientType.KTOR -> {
                    val ktorRequestCustomizer: ParameterSpec = ParameterSpec.builder(
                        "requestCustomizer",
                        LambdaTypeName.get(
                            HttpRequestBuilder::class.asTypeName(),
                            emptyList(),
                            Unit::class.asTypeName()
                        )
                    )
                        .defaultValue(CodeBlock.of("{}"))
                        .build()

                    clientExtensionFunction.receiver(ClassName(LIBRARY_CLIENT_KTOR_PACKAGE, "GraphQLKtorClient").parameterizedBy(STAR))
                        .addParameter(ktorRequestCustomizer)
                        .addStatement("return execute(request, requestCustomizer)")
                }
                GraphQLClientType.WEBCLIENT -> {
                    val webClientRequestCustomizer: ParameterSpec = ParameterSpec.builder(
                        "requestCustomizer",
                        LambdaTypeName.get(
                            ClassName("org.springframework.web.reactive.function.client", "WebClient", "RequestBodyUriSpec"),
                            emptyList(),
                            Unit::class.asTypeName()
                        )
                    )
                        .defaultValue(CodeBlock.of("{}"))
                        .build()
                    clientExtensionFunction.receiver(ClassName(LIBRARY_CLIENT_SPRING_PACKAGE, "GraphQLWebClient"))
                        .addParameter(webClientRequestCustomizer)
                        .addStatement("return execute(request, requestCustomizer)")
                }
                else -> {
                    clientExtensionFunction.receiver(ClassName(LIBRARY_CLIENT_PACKAGE, "GraphQLClient").parameterizedBy(STAR))
                        .addStatement("return execute(request)")
                }
            }
            fileSpec.addFunction(clientExtensionFunction.build())

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
