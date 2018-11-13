package com.expedia.graphql.schema.extensions

import com.expedia.graphql.schema.generator.functionFilters
import com.expedia.graphql.schema.generator.propertyFilters
import com.expedia.graphql.schema.hooks.NoopSchemaGeneratorHooks
import com.expedia.graphql.schema.hooks.SchemaGeneratorHooks
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.declaredMemberProperties

private val noopHooks = NoopSchemaGeneratorHooks()

internal fun KClass<*>.getValidProperties(hooks: SchemaGeneratorHooks = noopHooks) = this.declaredMemberProperties
    .filter { hooks.isValidProperty(it) }
    .filter { prop -> propertyFilters.all { it.invoke(prop) } }

internal fun KClass<*>.getValidFunctions(hooks: SchemaGeneratorHooks = noopHooks) = this.declaredMemberFunctions
    .filter { hooks.isValidFunction(it) }
    .filter { func -> functionFilters.all { it.invoke(func) } }

internal fun KClass<*>.canBeGraphQLInterface(): Boolean = this.java.isInterface

internal fun KClass<*>.canBeGraphQLUnion(): Boolean =
    this.canBeGraphQLInterface() && this.declaredMemberProperties.isEmpty() && this.declaredMemberFunctions.isEmpty()
