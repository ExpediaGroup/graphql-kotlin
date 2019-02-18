package com.expedia.graphql.generator.types

import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getSimpleName
import com.expedia.graphql.generator.extensions.getValidProperties
import com.expedia.graphql.generator.extensions.safeCast
import com.google.common.base.CaseFormat
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirective
import kotlin.reflect.KAnnotatedElement
import com.expedia.graphql.annotations.GraphQLDirective as GraphQLDirectiveAnnotation

internal class DirectiveTypeBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    internal fun directives(element: KAnnotatedElement): List<GraphQLDirective> =
        element.annotations.asSequence()
            .mapNotNull { it.getDirectiveInfo() }
            .map(this::getDirective)
            .toList()

    private fun getDirective(directiveInfo: DirectiveInfo): GraphQLDirective {

        val directiveClass = directiveInfo.directive.annotationClass

        val builder = GraphQLDirective.newDirective()
            .name(directiveInfo.effectiveName)
            .description(directiveInfo.directiveAnnotation.description)

        directiveInfo.directiveAnnotation.locations.forEach {
            builder.validLocation(it)
        }

        directiveClass.getValidProperties(config.hooks).forEach { prop ->
            val propertyName = prop.name
            val value = prop.call(directiveInfo.directive)
            val type = graphQLTypeOf(prop.returnType)

            val argument = GraphQLArgument.newArgument()
                .name(propertyName)
                .value(value)
                .type(type.safeCast())
                .build()

            builder.argument(argument)
        }

        return builder.build()
    }
}

private fun String.normalizeDirectiveName() = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, this)

private fun Annotation.getDirectiveInfo(): DirectiveInfo? = this.annotationClass.annotations
    .filterIsInstance(GraphQLDirectiveAnnotation::class.java)
    .map { DirectiveInfo(this, it) }
    .firstOrNull()

private data class DirectiveInfo(val directive: Annotation, val directiveAnnotation: GraphQLDirectiveAnnotation) {
    val effectiveName: String = when {
        directiveAnnotation.name.isNotEmpty() -> directiveAnnotation.name
        else -> directive.annotationClass.getSimpleName().normalizeDirectiveName()
    }
}
