/*
 * Copyright 2021 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.generator.internal.types

import com.expediagroup.graphql.generator.SchemaGenerator
import com.expediagroup.graphql.generator.internal.extensions.getPropertyAnnotations
import com.expediagroup.graphql.generator.internal.extensions.getSimpleName
import com.expediagroup.graphql.generator.internal.extensions.getValidProperties
import com.expediagroup.graphql.generator.internal.extensions.safeCast
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirective
import java.lang.reflect.Field
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import com.expediagroup.graphql.generator.annotations.GraphQLDirective as GraphQLDirectiveAnnotation

internal fun generateDirectives(
    generator: SchemaGenerator,
    element: KAnnotatedElement,
    location: DirectiveLocation,
    parentClass: KClass<*>? = null
): List<GraphQLDirective> {
    val annotations = when {
        element is KProperty<*> && parentClass != null -> element.getPropertyAnnotations(parentClass)
        else -> element.annotations
    }

    return annotations
        .mapNotNull { it.getDirectiveInfo() }
        .filter { it.directiveAnnotation.locations.contains(location) }
        .map { getDirective(generator, it) }
}

internal fun generateEnumValueDirectives(generator: SchemaGenerator, field: Field): List<GraphQLDirective> =
    field.annotations
        .mapNotNull { it.getDirectiveInfo() }
        .filter { it.directiveAnnotation.locations.contains(DirectiveLocation.ENUM_VALUE) }
        .map { getDirective(generator, it) }

private fun getDirective(generator: SchemaGenerator, directiveInfo: DirectiveInfo): GraphQLDirective {
    val directiveName = directiveInfo.effectiveName
    val directive = generator.directives.computeIfAbsent(directiveName) {
        val builder = GraphQLDirective.newDirective()
            .name(directiveInfo.effectiveName)
            .description(directiveInfo.directiveAnnotation.description)

        directiveInfo.directiveAnnotation.locations.forEach {
            builder.validLocation(it)
        }

        val directiveClass: KClass<out Annotation> = directiveInfo.directive.annotationClass
        val directiveArguments: List<KProperty<*>> = directiveClass.getValidProperties(generator.config.hooks)

        directiveArguments.forEach { prop ->
            val argument = generateDirectiveArgument(prop, directiveInfo, generator)
            builder.argument(argument)
        }

        builder.build()
    }

    return if (directive.arguments.isNotEmpty()) {
        // update args for this instance
        val builder = GraphQLDirective.newDirective(directive)
        directiveInfo.directive.annotationClass.getValidProperties(generator.config.hooks).forEach { prop ->
            val defaultArgument = directive.getArgument(prop.name)
            val value = prop.call(directiveInfo.directive)
            val argument = GraphQLArgument.newArgument(defaultArgument)
                .value(value)
                .build()
            builder.argument(argument)
        }
        builder.build()
    } else {
        directive
    }
}

private fun generateDirectiveArgument(prop: KProperty<*>, directiveInfo: DirectiveInfo, generator: SchemaGenerator): GraphQLArgument {
    val propertyName = prop.name
    val value = prop.call(directiveInfo.directive)
    val type = generateGraphQLType(generator, prop.returnType)

    return GraphQLArgument.newArgument()
        .name(propertyName)
        .value(value)
        .type(type.safeCast())
        .build()
}

private fun String.normalizeDirectiveName() = this.replaceFirstChar { it.lowercase() }

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
