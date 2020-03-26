package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.Field
import graphql.language.FieldDefinition
import graphql.language.FragmentDefinition
import graphql.language.FragmentSpread
import graphql.language.InlineFragment
import graphql.language.Selection
import graphql.language.SelectionSet

internal fun generateInterfaceTypeSpec(
    context: GraphQLClientGeneratorContext,
    interfaceName: String,
    kdoc: String?,
    fields: List<FieldDefinition>?,
    selectionSet: SelectionSet,
    implementations: List<String>
): TypeSpec {
    val interfaceTypeSpec = TypeSpec.interfaceBuilder(interfaceName)
    if (kdoc != null) {
        interfaceTypeSpec.addKdoc(kdoc)
    }

    val namedFragments = selectionSet.getSelectionsOfType(FragmentSpread::class.java).map { fragment ->
        context.queryDocument.getDefinitionsOfType(FragmentDefinition::class.java).find { it.name == fragment.name } ?: throw RuntimeException("fragment ${fragment.name} not found")
    }.associateBy { it.name }

    // find super selection set that contains
    // - directly selected fields
    // - fields referenced in named fragments referencing given interface
    val superSelectionSet = if (fields != null) {
        val selections: MutableList<Selection<*>> = selectionSet.getSelectionsOfType(Field::class.java).toMutableList()

        // interface fields
        namedFragments[interfaceName]?.let {
            selections.addAll(it.selectionSet.selections)
        }
        SelectionSet.newSelectionSet(selections).build()
    } else {
        SelectionSet.newSelectionSet().build()
    }

    // create interface with super fields
    val commonProperties = if (fields != null) {
        generatePropertySpecs(
            context = context,
            objectName = interfaceName,
            selectionSet = superSelectionSet,
            fieldDefinitions = fields
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
            val selections = superSelectionSet.selections.toMutableList()
            selections.addAll(namedFragment.selectionSet.selections)
            namedFragment.typeCondition to selections
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
        throw RuntimeException("query does not specify all polymorphic implementations - $interfaceName field selection is missing $notImplemented")
    }

    // generate implementations with final selection set
    val jsonSubTypesCodeBlock = CodeBlock.builder()
    implementationSelections.forEach { implementationName, (typeCondition, selections) ->
        if (!verifyTypeNameIsSelected(selections)) {
            throw RuntimeException("invalid selection set - $implementationName implementation of $interfaceName is missing __typename field in its selection set")
        }
        generateTypeName(context, typeCondition, SelectionSet.newSelectionSet(selections).build())
        val implementationTypeSpec = context.typeSpecs[implementationName]!!
        if (commonProperties.isNotEmpty()) {
            updateImplementationTypeSpecWithSuperInformation(context, interfaceName, implementationTypeSpec, commonProperties)
        }

        if (jsonSubTypesCodeBlock.isNotEmpty()) {
            jsonSubTypesCodeBlock.add(",")
        }
        val implementationClassName = context.classNameCache[implementationName]
        jsonSubTypesCodeBlock.add("com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = %T::class, name=%S)", implementationClassName, implementationName)
    }

    // add jackson annotations to handle deserialization
    interfaceTypeSpec.addAnnotation(AnnotationSpec.builder(JsonTypeInfo::class.java)
        .addMember("use = %T", JsonTypeInfo.Id.NAME::class.java)
        .addMember("include = %T", JsonTypeInfo.As.PROPERTY::class.java)
        .addMember("property = %S", "__typename")
        .build())
    interfaceTypeSpec.addAnnotation(AnnotationSpec.builder(JsonSubTypes::class.java)
        .addMember("value = [%L]", jsonSubTypesCodeBlock.build())
        .build())

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

    val updatedType = builder.build()
    context.typeSpecs[implementationTypeSpec.name!!] = updatedType
}
