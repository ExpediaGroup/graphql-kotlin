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

package com.expediagroup.graphql.generator.internal.state

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ClassInfoList
import java.io.Closeable
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

/**
 * This class should be used from a try-with-resouces block
 * or another closable object as the internal scan result can take up a lot of resources.
 */
internal class ClassScanner(supportedPackages: List<String>) : Closeable {

    @Suppress("Detekt.SpreadOperator")
    private val scanResult = ClassGraph()
        .enableAllInfo()
        .acceptPackages(*supportedPackages.toTypedArray())
        .scan()

    /**
     * Return true if there are no valid packages scanned
     */
    fun isEmptyScan() = scanResult.packageInfo.isEmpty()

    /**
     * Get the sub-types/implementations of specific KClass
     */
    fun getSubTypesOf(kclass: KClass<*>): List<KClass<*>> {
        val classInfo = scanResult.getClassInfo(kclass.jvmName) ?: return emptyList()

        return getImplementingClasses(classInfo)
            .union(classInfo.subclasses)
            .map { it.loadClass().kotlin }
            .filterNot { it.isAbstract }
    }

    /**
     * Find any class that has the specified annotation
     */
    fun getClassesWithAnnotation(annotation: KClass<*>) = scanResult.getClassesWithAnnotation(annotation.jvmName).map { it.loadClass().kotlin }

    /**
     * Clean up the scan result resources
     */
    override fun close() = scanResult.close()

    @Suppress("Detekt.SwallowedException")
    private fun getImplementingClasses(classInfo: ClassInfo) =
        try {
            classInfo.classesImplementing
        } catch (e: IllegalArgumentException) {
            // Ignored, just return empty list
            ClassInfoList.emptyList()
        }
}
