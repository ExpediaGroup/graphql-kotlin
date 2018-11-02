package com.expedia.graphql.schema.extensions

import com.expedia.graphql.annotations.GraphQLDefaultValue
import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLID
import com.expedia.graphql.annotations.GraphQLIgnore
import com.expedia.graphql.schema.exceptions.CouldNotGetNameOfAnnotationException
import com.expedia.graphql.schema.generator.isNotBlackListed
import com.expedia.graphql.schema.generator.types.defaultGraphQLScalars
import com.google.common.base.CaseFormat
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLInputType
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import com.expedia.graphql.annotations.GraphQLDirective as DirectiveAnnotation

internal fun KAnnotatedElement.graphQLDescription(): String? {
    val directiveNames = listOfDirectives().map { it.normalizeDirectiveName() }

    val description = this.findAnnotation<GraphQLDescription>()?.value

    return when {
        description != null && directiveNames.isNotEmpty() ->
            """$description
            |
            |Directives: ${directiveNames.joinToString(", ")}
            """.trimMargin()
        description == null && directiveNames.isNotEmpty() ->
            "Directives: ${directiveNames.joinToString(", ")}"
        else -> description
    }
}

private fun KAnnotatedElement.listOfDirectives(): List<String> {
    val directiveNames = mutableListOf(this.getDeprecationReason()?.let { "deprecated" })

    directiveNames.addAll(this.annotations
            .filter { it.getDirectiveInfo() != null }
            .map {
                when {
                    it.getDirectiveInfo()?.name != "" -> "@${it.getDirectiveInfo()?.name}"
                    else -> "@${it.annotationClass.simpleName}"
                }
            }
    )
    return directiveNames.filterNotNull()
}

internal fun KAnnotatedElement.getDeprecationReason(): String? {
    val annotation = this.findAnnotation<Deprecated>() ?: return null
    val builder = StringBuilder()
    builder.append(annotation.message)
    if (!annotation.replaceWith.expression.isBlank()) {
        builder.append(", replace with ")
        builder.append(annotation.replaceWith.expression)
    }
    return builder.toString()
}

internal fun KAnnotatedElement.isGraphQLIgnored() = this.findAnnotation<GraphQLIgnore>() != null

internal fun KAnnotatedElement.isGraphQLID() = this.findAnnotation<GraphQLID>() != null

internal fun KAnnotatedElement.hasDefaultValue() = this.findAnnotation<GraphQLDefaultValue>() != null

internal fun KAnnotatedElement.getDefaultValueAsString() = this.findAnnotation<GraphQLDefaultValue>()?.valueAsString ?: ""

internal fun Annotation.getDirectiveInfo() =
    this.annotationClass.annotations.find { it is DirectiveAnnotation } as? DirectiveAnnotation

internal fun KClass<out Annotation>.properties() = this.declaredMemberFunctions.filter(isNotBlackListed)

internal fun KAnnotatedElement.directives() =
    this.annotations.mapNotNull { annotation ->
        annotation.getDirectiveInfo()?.let { directiveInfo ->
            val builder = GraphQLDirective.newDirective()
            val name: String = if (directiveInfo.name.isNotEmpty()) {
                directiveInfo.name
            } else {
                annotation.annotationClass.simpleName ?: throw CouldNotGetNameOfAnnotationException(annotation.annotationClass)
            }
            builder.name(name.normalizeDirectiveName())
                .validLocations(*directiveInfo.locations)
                .description(directiveInfo.description)

            annotation::class.properties().forEach { prop ->
                val propertyName = prop.name
                val value = prop.call(annotation)
                @Suppress("Detekt.UnsafeCast")
                val type = defaultGraphQLScalars(prop.returnType) as GraphQLInputType
                builder.argument(GraphQLArgument.newArgument().name(propertyName).value(value).type(type).build())
            }

        builder.build()
    }
}

private fun String.normalizeDirectiveName() = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, this)
