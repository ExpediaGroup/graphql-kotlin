package com.expediagroup.graphql.generator

import com.expediagroup.graphql.annotations.GraphQLExtensionFunction
import com.expediagroup.graphql.generator.extensions.asExtensionFunction
import com.expediagroup.graphql.generator.extensions.getSimpleName
import com.expediagroup.graphql.generator.filters.functionFilters
import org.reflections.Reflections
import org.reflections.scanners.MethodAnnotationsScanner
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

internal class ExtensionFunctionMapper(supportedPackages: List<String>) {

    private val reflections = Reflections(supportedPackages, MethodAnnotationsScanner())

    // currently throwing an error when something is annotated incorrectly. Other option would be just to filter it out
    private val extensionFunctions: Map<String, List<KFunction<*>>> = reflections.getMethodsAnnotatedWith(GraphQLExtensionFunction::class.java)
        .map { it.asExtensionFunction() }
        .groupBy { it.parameters.first().type.getSimpleName() }

    fun getValidExtensionFunctions(kclass: KClass<*>): List<KFunction<*>> = (extensionFunctions[kclass.getSimpleName()]
        ?: listOf()).filter { func -> functionFilters.all { it.invoke(func) } }
}
