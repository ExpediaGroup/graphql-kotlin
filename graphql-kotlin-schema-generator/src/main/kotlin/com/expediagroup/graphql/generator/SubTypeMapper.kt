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

package com.expediagroup.graphql.generator

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfo
import io.github.classgraph.ClassInfoList
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

internal class SubTypeMapper(val supportedPackages: List<String>) {

    @Suppress("Detekt.SpreadOperator")
    private val scanResult = ClassGraph()
        .enableAllInfo()
        .whitelistPackages(*supportedPackages.toTypedArray())
        .scan()

    fun getSubTypesOf(kclass: KClass<*>): List<KClass<*>> {
        val classInfo = scanResult.getClassInfo(kclass.jvmName) ?: return emptyList()

        return getImplementingClasses(classInfo)
            .union(classInfo.subclasses)
            .map { it.loadClass().kotlin }
            .filterNot { it.isAbstract }
    }

    @Suppress("Detekt.SwallowedException")
    private fun getImplementingClasses(classInfo: ClassInfo) =
        try {
            classInfo.classesImplementing
        } catch (e: IllegalArgumentException) {
            // Ignored, just return empty list
            ClassInfoList.emptyList()
        }
}
