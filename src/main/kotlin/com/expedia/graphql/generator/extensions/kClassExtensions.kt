package com.expedia.graphql.generator.extensions

import com.expedia.graphql.exceptions.CouldNotGetNameOfKClassException
import com.expedia.graphql.generator.filters.functionFilters
import com.expedia.graphql.generator.filters.propertyFilters
import com.expedia.graphql.generator.filters.superclassFilters
import com.expedia.graphql.hooks.SchemaGeneratorHooks
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KVisibility
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.findParameterByName
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.full.superclasses

internal fun KClass<*>.getValidProperties(hooks: SchemaGeneratorHooks): List<KProperty<*>> =
    this.declaredMemberProperties
        .filter { hooks.isValidProperty(it) }
        .filter { prop -> propertyFilters.all { it.invoke(prop, this) } }

internal fun KClass<*>.getValidFunctions(hooks: SchemaGeneratorHooks): List<KFunction<*>> =
    this.declaredMemberFunctions
        .filter { hooks.isValidFunction(it) }
        .filter { func -> functionFilters.all { it.invoke(func) } }

internal fun KClass<*>.getValidSuperclasses(hooks: SchemaGeneratorHooks): List<KClass<*>> =
    this.superclasses
        .filter { hooks.isValidSuperclass(it) }
        .filter { kClass -> superclassFilters.all { it.invoke(kClass) } }

internal fun KClass<*>.findConstructorParamter(name: String): KParameter? =
    this.primaryConstructor?.findParameterByName(name)

internal fun KClass<*>.isInterface(): Boolean = this.java.isInterface

internal fun KClass<*>.isUnion(): Boolean =
    this.isInterface() && this.declaredMemberProperties.isEmpty() && this.declaredMemberFunctions.isEmpty()

internal fun KClass<*>.isEnum(): Boolean = this.isSubclassOf(Enum::class)

internal fun KClass<*>.isList(): Boolean = this.isSubclassOf(List::class)

internal fun KClass<*>.isArray(): Boolean = this.java.isArray

internal fun KClass<*>.isListType(): Boolean = this.isList() || this.isArray()

@Throws(CouldNotGetNameOfKClassException::class)
internal fun KClass<*>.getSimpleName(isInputClass: Boolean = false): String {
    val name = this.simpleName ?: throw CouldNotGetNameOfKClassException(this)

    return when {
        isInputClass -> "${name}Input"
        else -> name
    }
}

internal fun KClass<*>.getQualifiedName(): String = this.qualifiedName ?: ""

internal fun KClass<*>.isPublic(): Boolean = this.visibility == KVisibility.PUBLIC
