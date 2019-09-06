package com.expediagroup.graphql.generator

import org.reflections.Reflections
import kotlin.reflect.KClass

internal class SubTypeMapper(supportedPackages: List<String>) {

    private val reflections = Reflections(supportedPackages)

    fun getSubTypesOf(kclass: KClass<*>): List<KClass<*>> = reflections.getSubTypesOf(kclass.java).filterNot { it.kotlin.isAbstract }.map { it.kotlin }
}
