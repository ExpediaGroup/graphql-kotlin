package com.expedia.graphql.generator.types

import com.expedia.graphql.annotations.GraphQLDirective
import com.expedia.graphql.generator.SchemaGenerator
import com.expedia.graphql.generator.TypeBuilder
import com.expedia.graphql.generator.extensions.getSimpleName
import com.expedia.graphql.generator.extensions.getValidProperties
import com.google.common.base.CaseFormat
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLInputType
import kotlin.reflect.KAnnotatedElement

internal class DirectiveTypeBuilder(generator: SchemaGenerator) : TypeBuilder(generator) {

    internal fun directives(element: KAnnotatedElement): List<graphql.schema.GraphQLDirective> =
        element.annotations.asSequence()
            .mapNotNull { it.getDirectiveInfo() }
            .map(this::getDirective)
            .toList()

    private fun getDirective(directiveInfo: DirectiveInfo): graphql.schema.GraphQLDirective {

        val directiveClass = directiveInfo.directive.annotationClass

        val builder = graphql.schema.GraphQLDirective.newDirective()
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
                .type(type as? GraphQLInputType)
                .build()

            builder.argument(argument)
        }

        return builder.build()
    }
}

private fun String.normalizeDirectiveName() = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, this)

private fun Annotation.getDirectiveInfo(): DirectiveInfo? = this.annotationClass.annotations
    .filterIsInstance(GraphQLDirective::class.java)
    .map { DirectiveInfo(this, it) }
    .firstOrNull()

private data class DirectiveInfo(val directive: Annotation, val directiveAnnotation: GraphQLDirective) {
    val effectiveName: String = when {
        directiveAnnotation.name.isNotEmpty() -> directiveAnnotation.name
        else -> directive.annotationClass.getSimpleName().normalizeDirectiveName()
    }
}
