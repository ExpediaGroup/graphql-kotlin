package com.expedia.graphql.schema

import com.expedia.graphql.annotations.GraphQLContext
import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLIgnore
import com.expedia.graphql.annotations.GraphQLInstrumentationIgnore
import com.google.common.base.CaseFormat
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLInputType
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import com.expedia.graphql.annotations.GraphQLDirective as DirectiveAnnotation

internal fun KAnnotatedElement.graphQLDescription(): String? {
    val prefix = this.getDeprecationReason()?.let { "DEPRECATED" }
    val description = this.findAnnotation<GraphQLDescription>()?.value

    return if (null != description) {
        if (null != prefix) {
            "$prefix: $description"
        } else {
            description
        }
    } else {
        prefix
    }
}

internal fun KType.graphQLDescription(): String? = (classifier as? KClass<*>)?.graphQLDescription()

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

internal fun KAnnotatedElement.isGraphQLInstrumentable() = this.findAnnotation<GraphQLInstrumentationIgnore>() == null

internal fun Annotation.getDirectiveInfo() =
    this.annotationClass.annotations.find { it is DirectiveAnnotation } as? DirectiveAnnotation

internal fun KClass<out Annotation>.properties() = this.declaredMemberFunctions.filter(isNotBlackListed)

internal fun KAnnotatedElement.directives() =
    this.annotations.mapNotNull { annotation ->
        annotation.getDirectiveInfo()?.let { directiveInfo ->
            val builder = GraphQLDirective.newDirective()
            val name = if (directiveInfo.name.isNotEmpty()) {
                directiveInfo.name
            } else {
                annotation.annotationClass.simpleName
            }
            builder.name(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name))

            annotation::class.properties().forEach { prop ->
                val propertyName = prop.name
                val value = prop.call(annotation)
                @Suppress("Detekt.UnsafeCast")
                val type = graphQLScalar(prop.returnType) as GraphQLInputType
                builder.argument(GraphQLArgument.newArgument().name(propertyName).value(value).type(type).build())
            }

            builder.build()
        }
    }

internal fun KParameter.isGraphQLContext() = this.findAnnotation<GraphQLContext>() != null
