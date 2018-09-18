package com.expedia.graphql.schema

import com.expedia.graphql.annotations.GraphQLContext
import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLIgnore
import com.expedia.graphql.annotations.GraphQLInstrumentationIgnore
import com.expedia.graphql.annotations.GraphQLDirective as DirectiveAnnotation
import com.google.common.base.CaseFormat
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLInputType
import javax.validation.Valid
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmName

internal fun KAnnotatedElement.graphQLDescription(): String? {
    val prefix = this.getDeprecationReason()?.let { "DEPRECATED" }
    val description = this.findAnnotation<GraphQLDescription>()?.value

    val prefixedDescription = if (null != description) {
        if (null != prefix) {
            "$prefix: $description"
        } else {
            description
        }
    } else {
        prefix
    }.orEmpty()

    val validationDescription = when {
        this is KFunction<*> -> addValidationDescription(this)
        this is KClass<*> -> addValidationDescription(this)
        else -> ""
    }

    return if(validationDescription !=null && validationDescription != "") {
        """$prefixedDescription
            |
            |$validationDescription
            """.trimMargin()
    } else {
        prefixedDescription
    }
}

private fun addValidationDescription(kClass: KClass<*>): String? {

    fun Annotation.formatValidationProperties(): List<String> =
            annotationParser.findAll(this.toString())
                .map { it.value }
                .filterNot { it.startsWith("message=") }
                .filterNot { it == "" }
                .toList()

    fun List<Annotation>.formatValidationRules(): String =
        this.joinToStringIfNonEmpty(
            transform = {
                """${it.annotationClass.simpleName}
                   |
                   |${it.formatValidationProperties().joinToStringIfNonEmpty(prefix = "```\n", separator = "\n", postfix = "\n```\n")}
                """.trimMargin()},
            postfix = "\n"
        )


    val rules = kClass.declaredMemberProperties
        .map { it.name to it.getter.findValidationAnnotation() }
        .filter{ (_, validations) -> validations.isNotEmpty() }

      return rules.joinToStringIfNonEmpty(
              prefix = "\n#### Properties with validation:\n",
              transform =  {(propertyName, validationRules) ->
                  """**$propertyName**:
                    |
                    |${validationRules.formatValidationRules()}
                  """.trimMargin()},
              separator = "\n",
              postfix = "\n")
}

private fun <R> KProperty.Getter<R>.findValidationAnnotation()=
    this.annotations.filter { it.annotationClass.jvmName.startsWith("javax.validation")}

private fun addValidationDescription(func: KFunction<*>): String? {
    val paramWithValidation = func.parameters.filter { it.mustBeValid() }

    return paramWithValidation
            .joinToStringIfNonEmpty (
                prefix = "##### Arguments with validation:\n",
                transform = {"* ${it.name}\n"}
            )
}

internal fun KParameter.mustBeValid(): Boolean = this.findAnnotation<Valid>() != null

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

internal fun <T> List<T>.joinToStringIfNonEmpty(valueIfEmpty: String = "",
                                                separator: CharSequence = ", ",
                                                prefix: CharSequence = "",
                                                postfix: CharSequence = "",
                                                limit: Int = -1,
                                                truncated: CharSequence = "...",
                                                transform: ((T) -> CharSequence)? = null):String {
    return if (this.isNotEmpty() ) {
        this.joinToString(separator, prefix, postfix, limit, truncated, transform)
    } else {
        valueIfEmpty
    }
}

private val annotationParser = "\\w*=[{}a-zA-Z.\\[\\]\\d]*".toRegex()