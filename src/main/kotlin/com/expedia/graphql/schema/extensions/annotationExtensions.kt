package com.expedia.graphql.schema.extensions

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLID
import com.expedia.graphql.annotations.GraphQLIgnore
import com.expedia.graphql.schema.exceptions.CouldNotGetNameOfAnnotationException
import com.expedia.graphql.schema.generator.types.defaultGraphQLScalars
import com.expedia.graphql.schema.hooks.SchemaGeneratorHooks
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
            when {
                it.effectiveName.isNullOrEmpty().not() -> "@${it.effectiveName}"
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

private fun Annotation.getDirectiveInfo(): DirectiveInfo? {
    val directiveAnnotation = this.annotationClass.annotations.find { it is DirectiveAnnotation } as? DirectiveAnnotation
    return when {
        directiveAnnotation != null -> DirectiveInfo(this.annotationClass.simpleName ?: "", directiveAnnotation)
        else -> null
    }
}

internal fun KAnnotatedElement.directives(hooks: SchemaGeneratorHooks) =
    this.annotations.asSequence()
        .mapNotNull { it.getDirectiveInfo() }
        .map { it.getGraphQLDirective(hooks) }
        .toList()

@Throws(CouldNotGetNameOfAnnotationException::class)
private fun DirectiveInfo.getGraphQLDirective(hooks: SchemaGeneratorHooks): GraphQLDirective {
    val kClass: KClass<out DirectiveAnnotation> = this.annotation.annotationClass
    val builder = GraphQLDirective.newDirective()
    val name: String = this.effectiveName ?: throw CouldNotGetNameOfAnnotationException(kClass)

    @Suppress("Detekt.SpreadOperator")

    builder.name(name.normalizeDirectiveName())
        .validLocations(*this.annotation.locations)
        .description(this.annotation.description)

    kClass.getValidFunctions(hooks).forEach { kFunction ->
        val propertyName = kFunction.name
        val value = kFunction.call(kClass)
        @Suppress("Detekt.UnsafeCast")
        val type = defaultGraphQLScalars(kFunction.returnType) as GraphQLInputType
        val argument = GraphQLArgument.newArgument()
            .name(propertyName)
            .value(value)
            .type(type)
            .build()
        builder.argument(argument)
    }

    return builder.build()
}

private fun String.normalizeDirectiveName() = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, this)

private data class DirectiveInfo(private val name: String, val annotation: DirectiveAnnotation) {
    val effectiveName: String? = when {
        annotation.name.isNotEmpty() -> annotation.name
        name.isNotEmpty() -> name
        else -> null
    }
}
