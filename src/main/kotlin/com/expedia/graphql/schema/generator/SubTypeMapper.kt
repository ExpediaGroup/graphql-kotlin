package com.expedia.graphql.schema.generator

import org.reflections.Reflections
import kotlin.reflect.KClass

internal class SubTypeMapper(supportedPackages: List<String>) {

    private val reflections = Reflections(supportedPackages)

    fun getSubTypesOf(kclass: KClass<*>): MutableSet<out Class<out Any>> =
        reflections.getSubTypesOf(Class.forName(kclass.javaObjectType.name))
}
