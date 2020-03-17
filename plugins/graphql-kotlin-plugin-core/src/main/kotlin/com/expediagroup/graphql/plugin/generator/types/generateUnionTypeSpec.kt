package com.expediagroup.graphql.plugin.generator.types

import com.expediagroup.graphql.plugin.generator.GraphQLClientGeneratorContext
import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeSpec
import graphql.language.InlineFragment
import graphql.language.InterfaceTypeDefinition
import graphql.language.ObjectTypeDefinition
import graphql.language.SelectionSet
import graphql.language.UnionTypeDefinition

fun generateUnionTypeSpec(context: GraphQLClientGeneratorContext, unionDefinition: UnionTypeDefinition, selectionSet: SelectionSet): TypeSpec {
    val unionTypeSpec = TypeSpec.interfaceBuilder(unionDefinition.name)
    unionDefinition.description?.content?.let { kdoc ->
        unionTypeSpec.addKdoc(kdoc)
    }

    // TODO check if object was already created
    val jsonSubTypesCodeBlock = CodeBlock.builder()
    selectionSet.getSelectionsOfType(InlineFragment::class.java)
        .forEach { fragment ->
            val implementationDefinition = context.graphQLSchema.getType(fragment.typeCondition, ObjectTypeDefinition::class.java).get()
            val implementation = generateObjectTypeSpec(context, implementationDefinition, fragment.selectionSet)

            // TODO cleanup interface typename lookup
            if (jsonSubTypesCodeBlock.isNotEmpty()) {
                jsonSubTypesCodeBlock.add(",")
            }
            val implementationClassName = ClassName(context.packageName, "${context.rootType}.${implementation.name}")
            jsonSubTypesCodeBlock.add("com.fasterxml.jackson.annotation.JsonSubTypes.Type(value = %T::class, name=%S)", implementationClassName, implementation.name)
        }

    unionTypeSpec.addAnnotation(AnnotationSpec.Companion.builder(JsonTypeInfo::class.java)
        .addMember("use = %T", JsonTypeInfo.Id.NAME::class.java)
        .addMember("include = %T", JsonTypeInfo.As.PROPERTY::class.java)
        .addMember("property = %S", "__typename")
        .build())
    unionTypeSpec.addAnnotation(AnnotationSpec.Companion.builder(JsonSubTypes::class.java)
        .addMember("value = [%L]", jsonSubTypesCodeBlock.build())
        .build())

    val unionType = unionTypeSpec.build()
    context.typeSpecs[unionDefinition.name] = unionType
    return unionType
}
