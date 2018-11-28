package com.expedia.graphql.schema.extensions

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLID
import com.expedia.graphql.annotations.GraphQLIgnore
import com.expedia.graphql.schema.exceptions.CouldNotGetNameOfAnnotationException
import com.expedia.graphql.schema.generator.SchemaGenerator
import com.google.common.base.CaseFormat
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLInputType
import java.lang.reflect.Field
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation
import com.expedia.graphql.annotations.GraphQLDirective as DirectiveAnnotation

internal fun KAnnotatedElement.graphQLDescription(): String? = this.findAnnotation<GraphQLDescription>()?.value

internal fun Field.graphQLDescription(): String? = this.getAnnotation(GraphQLDescription::class.java)?.value

internal fun KAnnotatedElement.getDeprecationReason(): String? = this.findAnnotation<Deprecated>()?.getReason()

internal fun Field.getDeprecationReason(): String? = this.getDeclaredAnnotation(Deprecated::class.java)?.getReason()

internal fun Deprecated.getReason(): String? {
    val builder = StringBuilder()
    builder.append(this.message)
    if (!this.replaceWith.expression.isBlank()) {
        builder.append(", replace with ")
        builder.append(this.replaceWith.expression)
    }
    return builder.toString()
}

internal fun KAnnotatedElement.isGraphQLIgnored() = this.findAnnotation<GraphQLIgnore>() != null

internal fun KAnnotatedElement.isGraphQLID() = this.findAnnotation<GraphQLID>() != null

private fun Annotation.getDirectiveInfo(): DirectiveInfo? {
    return this.annotationClass.annotations
        .filterIsInstance(DirectiveAnnotation::class.java)
        .map { DirectiveInfo(this, it) }
        .firstOrNull()
}

internal fun KAnnotatedElement.directives(generator: SchemaGenerator) =
    this.annotations.asSequence()
        .mapNotNull { it.getDirectiveInfo() }
        .map { it.getGraphQLDirective(generator) }
        .toList()

internal fun KParameter.directives(generator: SchemaGenerator) =
    this.annotations.asSequence()
        .mapNotNull { it.getDirectiveInfo() }
        .map { it.getGraphQLDirective(generator) }
        .toList()

@Throws(CouldNotGetNameOfAnnotationException::class)
private fun DirectiveInfo.getGraphQLDirective(generator: SchemaGenerator): GraphQLDirective {
    val directiveClass = this.directive.annotationClass
    val name: String = this.effectiveName ?: throw CouldNotGetNameOfAnnotationException(directiveClass)

    @Suppress("Detekt.SpreadOperator")
    val builder = GraphQLDirective.newDirective()
        .name(name.normalizeDirectiveName())
        .validLocations(*this.directiveAnnotation.locations)
        .description(this.directiveAnnotation.description)

    directiveClass.getValidProperties(generator.config.hooks).forEach { prop ->
        val propertyName = prop.name
        val value = prop.call(this.directive)

        val type = generator.scalarType(prop.returnType)
            ?: generator.config.hooks.willGenerateGraphQLType(prop.returnType)
        val argument = GraphQLArgument.newArgument()
            .name(propertyName)
            .value(value)
            .type(type as? GraphQLInputType)
            .build()
        builder.argument(argument)
    }

    return builder.build()
}

private fun String.normalizeDirectiveName() = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, this)

private data class DirectiveInfo(val directive: Annotation, val directiveAnnotation: DirectiveAnnotation) {
    val effectiveName: String? = when {
        directiveAnnotation.name.isNotEmpty() -> directiveAnnotation.name
        directive.annotationClass.simpleName.isNullOrEmpty().not() -> directive.annotationClass.simpleName
        else -> null
    }
}
