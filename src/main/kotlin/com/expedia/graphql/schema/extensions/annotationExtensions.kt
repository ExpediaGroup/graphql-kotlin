package com.expedia.graphql.schema.extensions

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLID
import com.expedia.graphql.annotations.GraphQLIgnore
import com.expedia.graphql.schema.exceptions.CouldNotGetNameOfAnnotationException
import com.expedia.graphql.schema.generator.types.defaultGraphQLScalars
import com.google.common.base.CaseFormat
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLInputType
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
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
    val deprecationReason: String? = this.getDeprecationReason()?.let { "deprecated" }

    return this.annotations.asSequence()
        .mapNotNull { it.getDirectiveInfo() }
        .map {
            val directiveName = it.getDirectiveInfo()?.name
            val simpleName = it.annotationClass.simpleName

            when {
                directiveName.isNullOrEmpty().not() -> "@$directiveName"
                simpleName.isNullOrEmpty().not() -> "@$simpleName"
                else -> null
            }
        }
        .plus(deprecationReason)
        .filterNotNull()
        .toList()
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

internal fun Annotation.getDirectiveInfo(): DirectiveAnnotation? =
    this.annotationClass.annotations.find { it is DirectiveAnnotation } as? DirectiveAnnotation

internal fun KAnnotatedElement.directives() =
    this.annotations.asSequence()
        .mapNotNull { it.getDirectiveInfo() }
        .map { it.getGraphQLDirective() }
        .toList()

@Throws(CouldNotGetNameOfAnnotationException::class)
private fun DirectiveAnnotation.getGraphQLDirective(): GraphQLDirective {
    val kClass: KClass<out DirectiveAnnotation> = this.annotationClass
    val builder = GraphQLDirective.newDirective()
    val name: String = if (this.name.isNotEmpty()) {
        this.name
    } else {
        kClass.simpleName ?: throw CouldNotGetNameOfAnnotationException(kClass)
    }

    @Suppress("Detekt.SpreadOperator")
    builder.name(name.normalizeDirectiveName())
        .validLocations(*this.locations)
        .description(this.description)

    kClass.getValidFunctions().forEach { kFunction ->
        val propertyName = kFunction.name
        val value = kFunction.call(kClass)
        @Suppress("Detekt.UnsafeCast")
        val type = defaultGraphQLScalars(kFunction.returnType) as GraphQLInputType
        builder.argument(GraphQLArgument.newArgument().name(propertyName).value(value).type(type).build())
    }

    return builder.build()
}

private fun String.normalizeDirectiveName() = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, this)
