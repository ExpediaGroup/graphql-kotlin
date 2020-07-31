/*
 * Copyright 2019 Expedia, Inc
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

package com.expediagroup.graphql.generator.extensions

import com.expediagroup.graphql.exceptions.CouldNotGetNameOfKClassException
import com.expediagroup.graphql.generator.filters.functionFilters
import com.expediagroup.graphql.generator.filters.propertyFilters
import com.expediagroup.graphql.generator.filters.superclassFilters
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.superclasses

private const val INPUT_SUFFIX = "Input"

internal fun KClass<*>.getValidProperties(hooks: SchemaGeneratorHooks): List<KProperty<*>> =
    this.memberProperties
        .filter { prop -> hooks.isValidProperty(this, prop) }
        .filter { prop -> propertyFilters.all { it.invoke(prop, this) } }

internal fun KClass<*>.getValidFunctions(hooks: SchemaGeneratorHooks): List<KFunction<*>> =
    this.memberFunctions
        .filter { func -> hooks.isValidFunction(this, func) }
        .filter { func -> functionFilters.all { it.invoke(func) } }

internal fun KClass<*>.getValidSuperclasses(hooks: SchemaGeneratorHooks): List<KClass<*>> =
    this.superclasses
        .filter { hooks.isValidSuperclass(it) }
        .filter { kClass -> superclassFilters.all { it.invoke(kClass) } }
        .plus(this.superclasses.flatMap { it.getValidSuperclasses(hooks) })
        .distinct()

internal fun KClass<*>.findConstructorParameter(name: String): KParameter? =
    this.primaryConstructor?.findParameterByName(name)

internal fun KClass<*>.isInterface(): Boolean =
    this.java.isInterface || this.isAbstract || this.isSealed

internal fun KClass<*>.isUnion(): Boolean =
    this.isInterface() && this.declaredMemberProperties.isEmpty() && this.declaredMemberFunctions.isEmpty()

/**
 * Do not add interfaces as additional types if it expects all the types
 * to be input types. The isInteface() check works for both
 * GraphQL Interfaces and GraphQL Unions
 */
internal fun KClass<*>.isValidAdditionalType(inputType: Boolean): Boolean = !(inputType && this.isInterface())

internal fun KClass<*>.isEnum(): Boolean = this.isSubclassOf(Enum::class)

internal fun KClass<*>.isListType(): Boolean = this.isSubclassOf(List::class) || this.java.isArray

@Throws(CouldNotGetNameOfKClassException::class)
internal fun KClass<*>.getSimpleName(isInputClass: Boolean = false): String {
    val name = this.getGraphQLName()
        ?: this.simpleName
        ?: throw CouldNotGetNameOfKClassException(this)

    return when {
        isInputClass -> if (name.endsWith(INPUT_SUFFIX)) name else "$name$INPUT_SUFFIX"
        else -> name
    }
}

internal fun KClass<*>.getQualifiedName(): String = this.qualifiedName.orEmpty()

internal fun KClass<*>.isPublic(): Boolean = this.visibility == KVisibility.PUBLIC

internal fun KClass<*>.isNotPublic(): Boolean = this.isPublic().not()
