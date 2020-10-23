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

package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.expediagroup.graphql.plugin.generator.exceptions.InvalidFragmentException
import com.expediagroup.graphql.plugin.generator.exceptions.InvalidPolymorphicQueryException
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.Field
import graphql.language.FieldDefinition
import graphql.language.FragmentDefinition
import graphql.language.FragmentSpread
import graphql.language.InlineFragment
import graphql.language.Selection
import graphql.language.SelectionSet

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
    val interfaceTypeSpec = TypeSpec.interfaceBuilder(interfaceName)
    if (kdoc != null) {
        interfaceTypeSpec.addKdoc(kdoc)
    }

    val namedFragments = selectionSet.getSelectionsOfType(FragmentSpread::class.java).map { fragment ->
        // polymorphic selection set can contain selection set against interface or concrete types
        context.queryDocument.getDefinitionsOfType(FragmentDefinition::class.java)
            .find { it.name == fragment.name } ?: throw InvalidFragmentException(fragment.name, interfaceName)
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

    // check if all implementations are selected
    val notImplemented = implementations.minus(implementationSelections.keys)
    if (notImplemented.isNotEmpty()) {
        throw InvalidPolymorphicQueryException("query does not specify all polymorphic implementations - $interfaceName field selection is missing $notImplemented")
    }

    // generate implementations with final selection set
    val jsonSubTypesCodeBlock = CodeBlock.builder()
    implementationSelections.forEach { implementationName, (typeCondition, selections) ->
        val distinctSelections = selections.filterIsInstance(Field::class.java).distinctBy { if (it.alias != null) it.alias else it.name }
        if (!verifyTypeNameIsSelected(distinctSelections)) {
            throw InvalidPolymorphicQueryException("invalid polymorphic selection set - $implementationName implementation of $interfaceName is missing __typename field in its selection set")
        }
        val implementationClassName = generateTypeName(context, typeCondition, SelectionSet.newSelectionSet(distinctSelections).build()) as ClassName
        val simpleName = implementationClassName.simpleName.substringAfter('.')
        val implementationTypeSpec = context.typeSpecs[simpleName]!!
        updateImplementationTypeSpecWithSuperInformation(context, interfaceName, implementationTypeSpec, commonProperties)

        if (jsonSubTypesCodeBlock.isNotEmpty()) {
            jsonSubTypesCodeBlock.add(",")
        }

        // need unwrapped type name for the JsonSubTypes annotation, underlying field could be nullable
        val unwrappedClassName = implementationClassName.copy(nullable = false)
        // we point to original implementation name as that will be value from the __typename
        jsonSubTypesCodeBlock.add("com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = %T::class, name=%S)", unwrappedClassName, implementationName)
        context.objectsWithTypeNameSelection.add(implementationName)
    }

    // add jackson annotations to handle deserialization
    val jsonTypeInfoIdName = MemberName("com.fasterxml.jackson.annotation", "JsonTypeInfo.Id.NAME")
    val jsonTypeInfoAsProperty = MemberName("com.fasterxml.jackson.annotation", "JsonTypeInfo.As.PROPERTY")
    interfaceTypeSpec.addAnnotation(
        AnnotationSpec.builder(JsonTypeInfo::class.java)
            .addMember("use = %M", jsonTypeInfoIdName)
            .addMember("include = %M", jsonTypeInfoAsProperty)
            .addMember("property = %S", "__typename")
            .build()
    )
    interfaceTypeSpec.addAnnotation(
        AnnotationSpec.builder(JsonSubTypes::class.java)
            .addMember("value = [%L]", jsonSubTypesCodeBlock.build())
            .build()
    )

    return interfaceTypeSpec.build()
}

private fun verifyTypeNameIsSelected(selections: List<Selection<*>>) = selections.filterIsInstance(Field::class.java).any { it.name == "__typename" }

private fun updateImplementationTypeSpecWithSuperInformation(context: GraphQLClientGeneratorContext, interfaceName: String, implementationTypeSpec: TypeSpec, commonProperties: List<PropertySpec>) {
    val commonPropertyNames = commonProperties.map { it.name }

    val builder = TypeSpec.classBuilder(implementationTypeSpec.name!!)
    builder.addModifiers(implementationTypeSpec.modifiers)
    builder.addKdoc(implementationTypeSpec.kdoc)

    // TODO is there a better way to lookup interface class name?
    //  - cannot use typeNameCache as it was not populated yet
    builder.addSuperinterface(ClassName(context.packageName, "${context.rootType}.$interfaceName"))

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
    context.typeSpecs[implementationTypeSpec.name!!] = updatedType
}
