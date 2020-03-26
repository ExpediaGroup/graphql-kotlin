package com.expediagroup.graphql.plugin.generator

import com.expediagroup.graphql.plugin.generator.types.generateGraphQLObjectTypeSpec
import com.expediagroup.graphql.plugin.generator.types.generateVariableTypeSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.ObjectTypeDefinition
import graphql.language.OperationDefinition
import graphql.parser.Parser
import graphql.schema.idl.TypeDefinitionRegistry
import java.io.File

private const val LIBRARY_PACKAGE = "com.expediagroup.graphql.client"

class GraphQLClientGenerator(
    private val graphQLSchema: TypeDefinitionRegistry,
    private val config: GraphQLClientGeneratorConfig
) {
    private val documentParser: Parser = Parser()

    fun generate(queryFile: File): FileSpec {
        val queryConst = queryFile.readText()
        val queryDocument = documentParser.parseDocument(queryConst)

        val operationDefinitions = queryDocument.definitions.filterIsInstance(OperationDefinition::class.java)
        if (operationDefinitions.size > 1) {
            throw RuntimeException("GraphQL client does not support query files with multiple operations")
        }

        val fileSpec = FileSpec.builder(packageName = config.packageName, fileName = queryFile.nameWithoutExtension)
        operationDefinitions.forEach { operationDefinition ->
            val operationName = operationDefinition.name ?: "anonymous${operationDefinition.operation.name.toLowerCase().capitalize()}"
            val operationTypeName = operationName.capitalize()
            val context = GraphQLClientGeneratorContext(
                packageName = config.packageName,
                graphQLSchema = graphQLSchema,
                rootType = operationTypeName,
                queryDocument = queryDocument,
                allowDeprecated = config.allowDeprecated,
                scalarTypeToConverterMapping = config.scalarTypeToConverterMapping
            )

            val variableType: TypeSpec? = generateVariableTypeSpec(context, operationDefinition.variableDefinitions)

            val rootType = findRootType(operationDefinition)
            val graphQLResultTypeSpec = generateGraphQLObjectTypeSpec(context, rootType, operationDefinition.selectionSet, "${operationTypeName}Result")
            val kotlinResultTypeName = ClassName(context.packageName, "${context.rootType}.${graphQLResultTypeSpec.name}")

            val operationTypeSpec = TypeSpec.classBuilder(operationTypeName)
            val funSpec = FunSpec.builder(operationName.decapitalize())
                .returns(ClassName(LIBRARY_PACKAGE, "GraphQLResult").parameterizedBy(kotlinResultTypeName))
                .addModifiers(KModifier.SUSPEND)
            val variableCode = if (variableType != null) {
                funSpec.addParameter("variables", ClassName(config.packageName, "$operationTypeName.Variables"))
                operationTypeSpec.addType(variableType)
                "variables"
            } else {
                "null"
            }

            val queryConstName = operationName.toUpperUnderscore()
            funSpec.addStatement("return graphQLClient.executeOperation($queryConstName, \"$operationTypeName\", $variableCode)")

            operationTypeSpec.primaryConstructor(FunSpec.constructorBuilder()
                .addParameter("graphQLClient", ClassName(LIBRARY_PACKAGE, "GraphQLClient"))
                .build())
            operationTypeSpec.addProperty(PropertySpec.builder("graphQLClient", ClassName(LIBRARY_PACKAGE, "GraphQLClient"), KModifier.PRIVATE)
                .initializer("graphQLClient").build())
            operationTypeSpec.addFunction(funSpec.build())

            context.typeSpecs.forEach {
                operationTypeSpec.addType(it.value)
            }
            fileSpec.addProperty(PropertySpec.builder(queryConstName, STRING)
                .addModifiers(KModifier.CONST)
                .initializer("%S", queryConst).build())
            fileSpec.addType(operationTypeSpec.build())

            context.typeAliases.forEach { (_, alias) ->
                fileSpec.addTypeAlias(alias)
            }
        }

        return fileSpec.build()
    }

    private fun findRootType(operationDefinition: OperationDefinition): ObjectTypeDefinition {
        val operationNames = if (graphQLSchema.schemaDefinition().isPresent) {
            graphQLSchema.schemaDefinition().get().operationTypeDefinitions.associateBy({ it.name.toUpperCase() }, { it.typeName.name })
        } else {
            mapOf(
                OperationDefinition.Operation.QUERY.name to "Query",
                OperationDefinition.Operation.MUTATION to "Mutation",
                OperationDefinition.Operation.SUBSCRIPTION to "Subscription"
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
