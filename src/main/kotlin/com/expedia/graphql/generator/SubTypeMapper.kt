package com.expedia.graphql.generator

import org.reflections.Reflections
import kotlin.reflect.KClass

internal class SubTypeMapper(supportedPackages: List<String>) {

    private val reflections = Reflections(supportedPackages)

    fun getSubTypesOf(kclass: KClass<*>): List<Class<*>> =
        reflections.getSubTypesOf(Class.forName(kclass.javaObjectType.name))
            .filterNot { it.kotlin.isAbstract }
}
