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
import graphql.language.FragmentDefinition
import graphql.language.FragmentSpread
import graphql.language.InlineFragment
import graphql.language.InterfaceTypeDefinition
import graphql.language.ObjectTypeDefinition
import graphql.language.SelectionSet

fun generateInterfaceTypeSpec(context: GraphQLClientGeneratorContext, interfaceDefinition: InterfaceTypeDefinition, selectionSet: SelectionSet): TypeSpec {
    val interfaceTypeSpec = TypeSpec.interfaceBuilder(interfaceDefinition.name)
    interfaceDefinition.description?.content?.let { kdoc ->
        interfaceTypeSpec.addKdoc(kdoc)
    }

    val commonProperties: MutableList<PropertySpec> = mutableListOf()
    commonProperties.addAll(selectionSet.generatePropertySpecs(
        context,
        interfaceDefinition.fieldDefinitions,
        interfaceDefinition.name
    ))
    selectionSet.getSelectionsOfType(FragmentSpread::class.java)
        .forEach { fragment ->
            val fragmentDefinition = context.queryDocument
                .getDefinitionsOfType(FragmentDefinition::class.java)
                .find { it.name == fragment.name } ?: throw RuntimeException("fragment not found")
            commonProperties.addAll(fragmentDefinition.selectionSet.generatePropertySpecs(
                context,
                interfaceDefinition.fieldDefinitions,
                interfaceDefinition.name
            ))
        }
    if (commonProperties.isNotEmpty()) {
        interfaceTypeSpec.addProperties(commonProperties)
    }

    // TODO check if object was already created
    val jsonSubTypesCodeBlock = CodeBlock.builder()
    selectionSet.getSelectionsOfType(InlineFragment::class.java)
        .forEach { fragment ->
            val implementationDefinition = context.graphQLSchema.getType(fragment.typeCondition, ObjectTypeDefinition::class.java).get()
            val implementation = generateObjectTypeSpec(context, implementationDefinition, fragment.selectionSet)
            if (commonProperties.isNotEmpty()) {
                val builder = implementation.toBuilder()
                // TODO cleanup interface typename lookup
                builder.addSuperinterface(ClassName(context.packageName, "${context.rootType}.${interfaceDefinition.name}"))
                val constructor = FunSpec.constructorBuilder()
                commonProperties.forEach { commonProperty ->
                    val overriddenProperty = commonProperty.toBuilder().addModifiers(KModifier.OVERRIDE).build()
                    builder.addProperty(overriddenProperty)
                    constructor.addParameter(overriddenProperty.name, overriddenProperty.type)
                }
                implementation.primaryConstructor?.parameters?.forEach {
                    constructor.addParameter(it)
                }
                builder.primaryConstructor(constructor.build())
                context.typeSpecs[implementationDefinition.name] = builder.build()
            }
            // TODO cleanup interface typename lookup
            if (jsonSubTypesCodeBlock.isNotEmpty()) {
                jsonSubTypesCodeBlock.add(",")
            }
            val implementationClassName = ClassName(context.packageName, "${context.rootType}.${implementation.name}")
            jsonSubTypesCodeBlock.add("com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = %T::class, name=%S)", implementationClassName, implementation.name)
        }

    interfaceTypeSpec.addAnnotation(AnnotationSpec.Companion.builder(JsonTypeInfo::class.java)
        .addMember("use = %T", JsonTypeInfo.Id.NAME::class.java)
        .addMember("include = %T", JsonTypeInfo.As.PROPERTY::class.java)
        .addMember("property = %S", "__typename")
        .build())
    interfaceTypeSpec.addAnnotation(AnnotationSpec.Companion.builder(JsonSubTypes::class.java)
        .addMember("value = [%L]", jsonSubTypesCodeBlock.build())
        .build())

    val interfaceType = interfaceTypeSpec.build()
    context.typeSpecs[interfaceDefinition.name] = interfaceType
    return interfaceType
}
