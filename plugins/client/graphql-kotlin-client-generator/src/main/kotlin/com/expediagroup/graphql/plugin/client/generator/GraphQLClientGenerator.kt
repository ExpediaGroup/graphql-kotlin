/*
 * Copyright 2022 Expedia, Inc
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

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.plugin.client.generator.exceptions.MultipleOperationsInFileException
import com.expediagroup.graphql.plugin.client.generator.exceptions.SchemaUnavailableException
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
import graphql.parser.ParserOptions
import graphql.schema.idl.SchemaParser
import graphql.schema.idl.TypeDefinitionRegistry
import kotlinx.serialization.Required
import kotlinx.serialization.Serializable
import java.io.File

private const val CORE_TYPES_PACKAGE = "com.expediagroup.graphql.client.types"

/**
 * GraphQL client code generator that uses [KotlinPoet](https://github.com/square/kotlinpoet) to generate Kotlin classes based on the specified GraphQL queries.
 */
class GraphQLClientGenerator(
    schemaPath: String,
    private val config: GraphQLClientGeneratorConfig
) {
    private val documentParser: Parser = Parser()
    private val typeAliases: MutableMap<String, TypeAliasSpec> = mutableMapOf()
    private val sharedTypes: MutableMap<ClassName, List<TypeSpec>> = mutableMapOf()
    private var generateOptionalSerializer: Boolean = false
    private val graphQLSchema: TypeDefinitionRegistry
    private val parserOptions: ParserOptions = ParserOptions.newParserOptions().also { this.config.parserOptions(it) }.build()

    init {
        graphQLSchema = parseSchema(schemaPath)
    }

    /**
     * Generate GraphQL clients for the specified queries.
     */
    fun generate(queries: List<File>): List<FileSpec> {
        val result = mutableListOf<FileSpec>()
        for (query in queries) {
            result.addAll(generate(query))
        }

        // common shared types
        for ((className, typeSpecs) in sharedTypes) {
            val fileSpec = FileSpec.builder(className.packageName, className.simpleName)
            for (type in typeSpecs) {
                fileSpec.addType(type)
            }
            result.add(fileSpec.build())
        }

        // graphql type aliases
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
    internal fun generate(queryFile: File): List<FileSpec> {
        val queryConst = queryFile.readText().trim()
        val queryDocument = documentParser.parseDocument(queryConst, parserOptions)

        val operationDefinitions = queryDocument.definitions.filterIsInstance(OperationDefinition::class.java)
        if (operationDefinitions.size > 1) {
            throw MultipleOperationsInFileException(queryFile)
        }

        val fileSpecs = mutableListOf<FileSpec>()
        val operationFileSpec = FileSpec.builder(packageName = config.packageName, fileName = queryFile.nameWithoutExtension.capitalizeFirstChar())
        operationDefinitions.forEach { operationDefinition ->
            val capitalizedOperationName = operationDefinition.name?.capitalizeFirstChar() ?: queryFile.nameWithoutExtension.capitalizeFirstChar()
            val context = GraphQLClientGeneratorContext(
                packageName = config.packageName,
                graphQLSchema = graphQLSchema,
                operationName = capitalizedOperationName,
                queryDocument = queryDocument,
                allowDeprecated = config.allowDeprecated,
                customScalarMap = config.customScalarMap,
                serializer = config.serializer,
                useOptionalInputWrapper = config.useOptionalInputWrapper
            )
            val queryConstName = capitalizedOperationName.toUpperUnderscore()
            val queryConstProp = PropertySpec.builder(queryConstName, STRING)
                .addModifiers(KModifier.CONST)
                .initializer("%S", queryConst)
                .build()
            operationFileSpec.addProperty(queryConstProp)

            val rootType = findRootType(operationDefinition)
            val graphQLResponseTypeSpec = generateGraphQLObjectTypeSpec(context, rootType, operationDefinition.selectionSet, "Result")
            val kotlinResultTypeName = ClassName(context.packageName, "${context.operationName}.${graphQLResponseTypeSpec.name}")

            val operationTypeSpec = TypeSpec.classBuilder(capitalizedOperationName)
                .addAnnotation(Generated::class)
                .addSuperinterface(ClassName(CORE_TYPES_PACKAGE, "GraphQLClientRequest").parameterizedBy(kotlinResultTypeName))

            var queryProperty: PropertySpec = PropertySpec.builder("query", STRING, KModifier.OVERRIDE)
                .initializer("%N", queryConstProp)
                .build()
            var operationNameProperty: PropertySpec? = if (operationDefinition.name != null) {
                PropertySpec.builder("operationName", STRING, KModifier.OVERRIDE)
                    .initializer("%S", operationDefinition.name)
                    .build()
            } else {
                null
            }

            if (config.serializer == GraphQLSerializer.KOTLINX) {
                operationTypeSpec.addAnnotation(Serializable::class)
                queryProperty = queryProperty.toBuilder().addAnnotation(Required::class).build()
                operationNameProperty = operationNameProperty?.toBuilder()?.addAnnotation(Required::class)?.build()
            }

            operationTypeSpec.addProperty(queryProperty)
            operationNameProperty?.let {
                operationTypeSpec.addProperty(it)
            }

            val variableType: TypeSpec? = generateVariableTypeSpec(context, operationDefinition.variableDefinitions)
            if (variableType != null) {
                operationTypeSpec.addType(variableType)

                val variablesClassName = ClassName(config.packageName, "$capitalizedOperationName.Variables")
                val variablesProperty = PropertySpec.builder("variables", variablesClassName, KModifier.OVERRIDE)
                    .initializer("variables")
                    .build()
                operationTypeSpec.addProperty(variablesProperty)

                val constructor = FunSpec.constructorBuilder()
                    .addParameter("variables", variablesClassName)
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
            operationTypeSpec.addType(graphQLResponseTypeSpec)

            val polymorphicTypes = mutableListOf<ClassName>()
            for ((superClassName, implementations) in context.polymorphicTypes) {
                polymorphicTypes.add(superClassName)
                val polymorphicTypeSpec = FileSpec.builder(superClassName.packageName, superClassName.simpleName)
                for (implementation in implementations) {
                    polymorphicTypes.add(implementation)
                    context.typeSpecs[implementation]?.let { typeSpec ->
                        polymorphicTypeSpec.addType(typeSpec)
                    }
                }
                fileSpecs.add(polymorphicTypeSpec.build())
            }
            context.typeSpecs.minus(polymorphicTypes).forEach { (className, typeSpec) ->
                val outputTypeFileSpec = FileSpec.builder(className.packageName, className.simpleName)
                    .addType(typeSpec)
                    .build()
                fileSpecs.add(outputTypeFileSpec)
            }
            operationFileSpec.addType(operationTypeSpec.build())
            fileSpecs.add(operationFileSpec.build())

            // shared types
            sharedTypes.putAll(context.enumClassToTypeSpecs.mapValues { listOf(it.value) })
            sharedTypes.putAll(context.inputClassToTypeSpecs.mapValues { listOf(it.value) })
            context.scalarClassToConverterTypeSpecs
                .values
                .forEach {
                    when (it) {
                        is ScalarConverterInfo.JacksonConvertersInfo -> {
                            sharedTypes[it.serializerClassName] = listOf(it.serializerTypeSpec)
                            sharedTypes[it.deserializerClassName] = listOf(it.deserializerTypeSpec)
                        }
                        is ScalarConverterInfo.KotlinxSerializerInfo -> {
                            sharedTypes[it.serializerClassName] = listOf(it.serializerTypeSpec)
                        }
                    }
                }
            typeAliases.putAll(context.typeAliases)

            if (context.requireOptionalSerializer) {
                generateOptionalSerializer = true
//                customScalarSerializers.putAll(context.scalarClassToConverterTypeSpecs)
                sharedTypes.putAll(context.optionalSerializers.mapValues { listOf(it.value) })
            }
        }
        return fileSpecs
    }

    private fun findRootType(operationDefinition: OperationDefinition): ObjectTypeDefinition {
        val operationNames = if (graphQLSchema.schemaDefinition().isPresent) {
            graphQLSchema.schemaDefinition().get().operationTypeDefinitions.associateBy({ it.name.uppercase() }, { it.typeName.name })
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

    private fun parseSchema(path: String): TypeDefinitionRegistry {
        val schemaFile = File(path)
        return if (schemaFile.isFile) {
            SchemaParser().parse(schemaFile)
        } else {
            val schemaInputStream = this.javaClass.classLoader.getResourceAsStream(path) ?: throw SchemaUnavailableException(path)
            SchemaParser().parse(schemaInputStream)
        }
    }
}

/**
 * This is the reccommended approach now with the deprecation of String.capitalize from the
 * Kotlin stdlib in version 1.5.
 */
internal fun String.capitalizeFirstChar(): String = replaceFirstChar { if (it.isLowerCase()) it.uppercaseChar() else it }

internal fun String.toUpperUnderscore(): String {
    val builder = StringBuilder()
    val nameCharArray = this.toCharArray()
    for ((index, c) in nameCharArray.withIndex()) {
        if (c.isUpperCase() && index > 0) {
            if (nameCharArray[index - 1].isLowerCase() || (index < nameCharArray.size - 1 && nameCharArray[index + 1].isLowerCase())) {
                builder.append("_")
            }
        }
        builder.append(c.uppercaseChar())
    }
    return builder.toString()
}
