/*
 * Copyright 2022 Expedia, Inc
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
import com.expediagroup.graphql.generator.directives.DirectiveMetaInformation
import com.expediagroup.graphql.generator.exceptions.InvalidDirectiveLocationException
import com.expediagroup.graphql.generator.internal.extensions.getPropertyAnnotations
import com.expediagroup.graphql.generator.internal.extensions.getValidProperties
import com.expediagroup.graphql.generator.internal.extensions.safeCast
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLAppliedDirective
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLDirective
import java.lang.reflect.Field
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.hasAnnotation
import com.expediagroup.graphql.generator.annotations.GraphQLDirective as GraphQLDirectiveAnnotation

internal fun generateDirectives(
    generator: SchemaGenerator,
    element: KAnnotatedElement,
    location: DirectiveLocation,
    parentClass: KClass<*>? = null
): List<GraphQLAppliedDirective> {
    val annotations = when {
        element is KProperty<*> && parentClass != null -> element.getPropertyAnnotations(parentClass)
        else -> element.annotations
    }

    return annotations
        .mapNotNull { it.getDirectiveInfo() }
        .map {
            if (!it.directiveAnnotation.locations.contains(location)) {
                throw InvalidDirectiveLocationException(it.effectiveName, it.directiveAnnotation.locations, location, element.toString())
            }
            getDirective(generator, it)
        }
}

internal fun generateEnumValueDirectives(generator: SchemaGenerator, field: Field, enumName: String): List<GraphQLAppliedDirective> =
    field.annotations
        .mapNotNull { it.getDirectiveInfo() }
        .map {
            if (!it.directiveAnnotation.locations.contains(DirectiveLocation.ENUM_VALUE)) {
                throw InvalidDirectiveLocationException(it.effectiveName, it.directiveAnnotation.locations, DirectiveLocation.ENUM_VALUE, "$enumName.${field.name}")
            }
            getDirective(generator, it)
        }

private fun getDirective(generator: SchemaGenerator, directiveInfo: DirectiveMetaInformation): GraphQLAppliedDirective {
    val directiveName = directiveInfo.effectiveName
    val directiveFromHook = generator.config.hooks.willGenerateDirective(directiveInfo)
        ?.let { generator.directives.putIfAbsent(directiveName, it) }

    val directive = directiveFromHook ?: generator.directives.computeIfAbsent(directiveName) {
        val builder = GraphQLDirective.newDirective()
            .name(directiveInfo.effectiveName)
            .description(directiveInfo.directiveAnnotation.description)
            .repeatable(directiveInfo.repeatable)

        directiveInfo.directiveAnnotation.locations.forEach {
            builder.validLocation(it)
        }

        val directiveClass: KClass<out Annotation> = directiveInfo.directive.annotationClass
        val directiveArguments: List<KProperty<*>> = directiveClass.getValidProperties(generator.config.hooks)

        directiveArguments.forEach { prop ->
            val argument = generateDirectiveArgument(prop, generator)
            builder.argument(argument)
        }

        builder.build()
    }

    val appliedDirective = directive.toAppliedDirective()
    return if (directive.arguments.isNotEmpty()) {
        appliedDirective.transform { builder ->
            directiveInfo.directive.annotationClass.getValidProperties(generator.config.hooks).forEach { prop ->
                directive.getArgument(prop.name)
                    ?.toAppliedArgument()
                    ?.transform { argumentBuilder ->
                        val value = prop.call(directiveInfo.directive)
                        argumentBuilder.valueProgrammatic(value)
                    }
                    ?.let { appliedDirectiveArgument ->
                        builder.argument(appliedDirectiveArgument)
                    }
            }
        }
    } else {
        appliedDirective
    }
}

private fun generateDirectiveArgument(prop: KProperty<*>, generator: SchemaGenerator): GraphQLArgument {
    val propertyName = prop.name
    val type = generateGraphQLType(generator, prop.returnType, GraphQLKTypeMetadata(inputType = true, isDirective = true))

    // default directive argument values are unsupported
    // https://github.com/ExpediaGroup/graphql-kotlin/issues/53
    return GraphQLArgument.newArgument()
        .name(propertyName)
        .type(type.safeCast())
        .build()
}

private fun Annotation.getDirectiveInfo(): DirectiveMetaInformation? = this.annotationClass.annotations
    .filterIsInstance(GraphQLDirectiveAnnotation::class.java)
    .map { graphqlDirective ->
        val isRepeatable = this.annotationClass.hasAnnotation<Repeatable>()
        DirectiveMetaInformation(this, graphqlDirective, isRepeatable)
    }
    .firstOrNull()
