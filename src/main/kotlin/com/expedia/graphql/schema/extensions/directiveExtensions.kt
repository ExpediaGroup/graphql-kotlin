package com.expedia.graphql.schema.extensions

import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.exceptions.CouldNotGetNameOfAnnotationException
import com.expedia.graphql.schema.generator.SchemaGenerator
import com.google.common.base.CaseFormat
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLInputType
import kotlin.reflect.KAnnotatedElement

internal fun KAnnotatedElement.directives(generator: SchemaGenerator) =
    this.annotations.asSequence()
        .mapNotNull { it.getDirectiveInfo() }
        .map { it.getGraphQLDirective(generator) }
        .toList()

private fun Annotation.getDirectiveInfo(): DirectiveInfo? {
    return this.annotationClass.annotations
        .filterIsInstance(GraphQLDirective::class.java)
        .map { DirectiveInfo(this, it) }
        .firstOrNull()
}

@Throws(CouldNotGetNameOfAnnotationException::class)
private fun DirectiveInfo.getGraphQLDirective(generator: SchemaGenerator): graphql.schema.GraphQLDirective {
    val directiveClass = this.directive.annotationClass
    val name: String = this.effectiveName ?: throw CouldNotGetNameOfAnnotationException(directiveClass)

    @Suppress("Detekt.SpreadOperator")
    val builder = graphql.schema.GraphQLDirective.newDirective()
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

private data class DirectiveInfo(val directive: Annotation, val directiveAnnotation: GraphQLDirective) {
    val effectiveName: String? = when {
        directiveAnnotation.name.isNotEmpty() -> directiveAnnotation.name
        directive.annotationClass.simpleName.isNullOrEmpty().not() -> directive.annotationClass.simpleName
        else -> null
    }
}
