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

package com.expediagroup.graphql.plugin.client.generator.types

import com.expediagroup.graphql.client.Generated
import com.expediagroup.graphql.plugin.client.generator.GraphQLClientGeneratorContext
import com.expediagroup.graphql.plugin.client.generator.GraphQLSerializer
import com.expediagroup.graphql.plugin.client.generator.exceptions.InvalidFragmentException
import com.expediagroup.graphql.plugin.client.generator.exceptions.MissingTypeNameException
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.Field
import graphql.language.FieldDefinition
import graphql.language.FragmentDefinition
import graphql.language.FragmentSpread
import graphql.language.InlineFragment
import graphql.language.Selection
import graphql.language.SelectionSet
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val logger: Logger = LoggerFactory.getLogger("com.expediagroup.graphql.plugin.client.generator.types.GenerateInterfaceTypeSpec")

/**
 * Generate interface [TypeSpec] based on the available field definitions and selection set. Generates all implementing classes as well.
 *
 * In order to generate interface:
 * - all implementing types have to specified in the query
 * - __typename has to be explicitly specified in the query
 * - all polymorphic types need to have consistent selection set within the query (i.e. you cannot select same polymorphic type with different selection sets in single query).
 *
 * @see generateInterfaceTypeSpec
 * @see generateGraphQLUnionTypeSpec
 */
internal fun generateInterfaceTypeSpec(
    context: GraphQLClientGeneratorContext,
    interfaceName: String,
    kdoc: String?,
    fields: List<FieldDefinition> = emptyList(),
    selectionSet: SelectionSet,
    implementations: List<String>
): TypeSpec {
    val interfaceTypeSpec = if (context.serializer == GraphQLSerializer.KOTLINX) {
        TypeSpec.classBuilder(interfaceName)
            .addModifiers(KModifier.SEALED)
            .addAnnotation(Generated::class)
            .addAnnotation(Serializable::class)
    } else {
        TypeSpec.interfaceBuilder(interfaceName)
            .addAnnotation(Generated::class)
    }

    if (kdoc != null) {
        interfaceTypeSpec.addKdoc("%L", kdoc)
    }

    val namedFragments = selectionSet.getSelectionsOfType(FragmentSpread::class.java).map { fragment ->
        // polymorphic selection set can contain selection set against interface or concrete types
        context.queryDocument.getDefinitionsOfType(FragmentDefinition::class.java)
            .find { it.name == fragment.name } ?: throw InvalidFragmentException(context.operationName, fragment.name, interfaceName)
    }.associateBy { it.typeCondition.name }

    // find super selection set that contains
    // - directly selected fields
    // - fields referenced in named fragments referencing given interface
    val selections: MutableList<Selection<*>> = selectionSet.getSelectionsOfType(Field::class.java).toMutableList()

    // interface fields
    namedFragments[interfaceName]?.let {
        selections.addAll(it.selectionSet.selections)
    }
    val superSelectionSet = SelectionSet.newSelectionSet(selections).build()

    // create interface with super fields
    val commonProperties = if (fields.isNotEmpty()) {
        generatePropertySpecs(
            context = context,
            objectName = interfaceName,
            selectionSet = superSelectionSet,
            fieldDefinitions = fields,
            abstract = true
        )
    } else {
        emptyList()
    }
    interfaceTypeSpec.addProperties(commonProperties)

    // generate selection sets for implementations that includes
    // - super selections
    // - named fragment selections
    // - inline fragment selections
    val implementationSelections = namedFragments.filterKeys { it != interfaceName }
        .mapValues { (_, namedFragment) ->
            val fragmentSelections = superSelectionSet.selections.toMutableList()
            fragmentSelections.addAll(namedFragment.selectionSet.selections)
            namedFragment.typeCondition to fragmentSelections
        }.toMutableMap()
    selectionSet.getSelectionsOfType(InlineFragment::class.java).forEach { fragment ->
        val existing = implementationSelections.computeIfAbsent(fragment.typeCondition.name) {
            fragment.typeCondition to superSelectionSet.selections.toMutableList()
        }
        existing.second.addAll(fragment.selectionSet.selections)
    }

    // log warning if not all implementations are selected
    val notImplemented = implementations.minus(implementationSelections.keys)
    if (notImplemented.isNotEmpty()) {
        logger.warn("Operation ${context.operationName} does not specify all polymorphic implementations - field selection on $interfaceName is missing $notImplemented")
    }

    // generate implementations with final selection set
    val jsonSubTypesCodeBlock = CodeBlock.builder()
    implementationSelections.forEach { implementationName, (typeCondition, selections) ->
        val distinctSelections = selections.filterIsInstance(Field::class.java).distinctBy { if (it.alias != null) it.alias else it.name }
        if (!verifyTypeNameIsSelected(distinctSelections)) {
            throw MissingTypeNameException(context.operationName, implementationName, interfaceName)
        }
        var implementationClassName = generateTypeName(context, typeCondition, SelectionSet.newSelectionSet(distinctSelections).build())
        if (implementationClassName.isNullable) {
            implementationClassName = implementationClassName.copy(nullable = false)
        }
        updateImplementationTypeSpecWithSuperInformation(context, interfaceName, implementationName, implementationClassName as ClassName, commonProperties)

        if (jsonSubTypesCodeBlock.isNotEmpty()) {
            jsonSubTypesCodeBlock.add(",")
        }

        // need unwrapped type name for the JsonSubTypes annotation, underlying field could be nullable
        val unwrappedClassName = implementationClassName.copy(nullable = false)
        // we point to original implementation name as that will be value from the __typename
        jsonSubTypesCodeBlock.add("com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = %T::class, name=%S)", unwrappedClassName, implementationName)
    }

    val fallbackClassName = generateFallbackImplementation(context, interfaceName, commonProperties)
    if (context.serializer == GraphQLSerializer.JACKSON) {
        // add jackson annotations to handle deserialization
        val jsonTypeInfoIdName = MemberName("com.fasterxml.jackson.annotation", "JsonTypeInfo.Id.NAME")
        val jsonTypeInfoAsProperty = MemberName("com.fasterxml.jackson.annotation", "JsonTypeInfo.As.PROPERTY")
        interfaceTypeSpec.addAnnotation(
            AnnotationSpec.builder(JsonTypeInfo::class.java)
                .addMember("use = %M", jsonTypeInfoIdName)
                .addMember("include = %M", jsonTypeInfoAsProperty)
                .addMember("property = %S", "__typename")
                .addMember("defaultImpl = %T::class", fallbackClassName)
                .build()
        )
        interfaceTypeSpec.addAnnotation(
            AnnotationSpec.builder(JsonSubTypes::class.java)
                .addMember("value = [%L]", jsonSubTypesCodeBlock.build())
                .build()
        )
    }

    return interfaceTypeSpec.build()
}

private fun verifyTypeNameIsSelected(selections: List<Selection<*>>) = selections.filterIsInstance(Field::class.java).any { it.name == "__typename" }

private fun updateImplementationTypeSpecWithSuperInformation(
    context: GraphQLClientGeneratorContext,
    interfaceName: String,
    implementationName: String,
    implementationClassName: ClassName,
    commonProperties: List<PropertySpec>
) {
    val commonPropertyNames = commonProperties.map { it.name }
    val implementationTypeSpec = context.typeSpecs[implementationClassName]!!

    val builder = implementationTypeSpec.toBuilder()
    val superClassName = ClassName("${context.packageName}.${context.operationName.lowercase()}", interfaceName)
    if (context.serializer == GraphQLSerializer.KOTLINX) {
        builder.addAnnotation(
            AnnotationSpec.builder(SerialName::class)
                .addMember("value = %S", implementationName)
                .build()
        )
            .superclass(superClassName)
    } else {
        builder.addSuperinterface(superClassName)
    }

    if (implementationTypeSpec.propertySpecs.isNotEmpty()) {
        val constructor = FunSpec.constructorBuilder()
        implementationTypeSpec.propertySpecs.forEach { property ->
            val updatedProperty = if (commonPropertyNames.contains(property.name)) {
                property.toBuilder().addModifiers(KModifier.OVERRIDE).build()
            } else {
                property
            }
            builder.addProperty(updatedProperty)
            constructor.addParameter(updatedProperty.name, updatedProperty.type)
        }
        builder.primaryConstructor(constructor.build())
    }

    val updatedType = builder.build()
    context.polymorphicTypes[superClassName]?.add(implementationClassName)
    context.typeSpecs[implementationClassName] = updatedType
}

private fun generateFallbackImplementation(context: GraphQLClientGeneratorContext, interfaceName: String, commonProperties: List<PropertySpec>): ClassName {
    val fallbackTypeName = "Default${interfaceName}Implementation"
    val superClassName = ClassName("${context.packageName}.${context.operationName.lowercase()}", interfaceName)
    val fallbackClassName = ClassName("${context.packageName}.${context.operationName.lowercase()}", fallbackTypeName)
    val fallbackType = TypeSpec.classBuilder(fallbackTypeName)
        .addAnnotation(Generated::class)
        .addKdoc("Fallback $interfaceName implementation that will be used when unknown/unhandled type is encountered.")
        .also {
            if (context.serializer == GraphQLSerializer.KOTLINX) {
                it.addAnnotation(Serializable::class)
                    .superclass(superClassName)
                    .addKdoc("\n\nNOTE: This fallback logic has to be manually registered with the instance of GraphQLClientKotlinxSerializer. See documentation for details.")
            } else {
                it.addSuperinterface(superClassName)
            }

            if (commonProperties.isNotEmpty()) {
                it.addModifiers(KModifier.DATA)
            }
        }
        .addProperties(
            commonProperties.map { abstractProperty ->
                abstractProperty.toBuilder()
                    .initializer(abstractProperty.name)
                    .addModifiers(KModifier.OVERRIDE)
                    .also {
                        it.modifiers.remove(KModifier.ABSTRACT)
                    }
                    .build()
            }
        )
        .primaryConstructor(
            FunSpec.constructorBuilder()
                .addParameters(
                    commonProperties.map { propertySpec ->
                        val constructorParameter = ParameterSpec.builder(propertySpec.name, propertySpec.type)
                        val className = propertySpec.type as? ClassName
                        if (propertySpec.type.isNullable) {
                            constructorParameter.defaultValue("null")
                        } else if (className != null && context.enumClassToTypeSpecs.keys.contains(className)) {
                            constructorParameter.defaultValue("%M", className.member(UNKNOWN_VALUE))
                        }
                        constructorParameter.build()
                    }
                )
                .build()
        )
        .build()
    context.polymorphicTypes[superClassName]?.add(fallbackClassName)
    context.typeSpecs[fallbackClassName] = fallbackType
    return fallbackClassName
}
