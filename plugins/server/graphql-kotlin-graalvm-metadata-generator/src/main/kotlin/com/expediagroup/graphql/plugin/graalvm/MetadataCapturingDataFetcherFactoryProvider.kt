/*
 * Copyright 2023 Expedia, Inc
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

package com.expediagroup.graphql.plugin.graalvm

import com.expediagroup.graphql.generator.execution.SimpleKotlinDataFetcherFactoryProvider
import graphql.schema.DataFetcherFactory
import io.github.classgraph.ScanResult
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.javaType
import kotlin.reflect.jvm.javaMethod

internal class MetadataCapturingDataFetcherFactoryProvider(val scanResult: ScanResult, val supportedPackages: List<String>) : SimpleKotlinDataFetcherFactoryProvider() {

    private val reflectMetadataMap: MutableMap<String, MutableClassMetadata> = HashMap()
    private val additionalInputTypes: MutableSet<String> = HashSet()
    // we need to capture enums
    private val additionalTypes: MutableSet<String> = HashSet()

    @OptIn(ExperimentalStdlibApi::class)
    override fun functionDataFetcherFactory(target: Any?, kClass: KClass<*>, kFunction: KFunction<*>): DataFetcherFactory<Any?> {
        val methodName = kFunction.javaMethod!!.name
        val classMetadata = reflectMetadataMap.getOrPut(kClass.java.name) { MutableClassMetadata(name = kClass.java.name, methods = ArrayList()) }
        val methodArguments = kFunction.javaMethod?.parameters?.map { it.type.name } ?: emptyList()
        val parameterizedArguments = kFunction.parameters.mapNotNull { it.type.arguments.firstOrNull() }.map { it.type!!.javaType.typeName }

        additionalInputTypes.addAll(methodArguments)
        additionalInputTypes.addAll(parameterizedArguments)
        additionalTypes.add(kFunction.returnType.javaType.typeName)
        classMetadata.methods?.add(
            MethodMetadata(
                name = methodName,
                parameterTypes = methodArguments
            )
        )
        // add synthetic methods
        kClass.java.methods.filter { it.name.startsWith("$methodName$") }.forEach { defaultMethod ->
            val syntheticMethodArgs = defaultMethod.parameters?.map { it.type.name } ?: emptyList()
            classMetadata.methods?.add(
                MethodMetadata(
                    name = defaultMethod.name,
                    parameterTypes = syntheticMethodArgs
                )
            )
        }
        return super.functionDataFetcherFactory(target, kClass, kFunction)
    }

    override fun propertyDataFetcherFactory(kClass: KClass<*>, kProperty: KProperty<*>): DataFetcherFactory<Any?> {
        val classMetadata = reflectMetadataMap.getOrPut(kClass.java.name) { MutableClassMetadata(name = kClass.java.name, methods = ArrayList()) }
        classMetadata.allDeclaredFields = true
        classMetadata.methods?.add(
            MethodMetadata(
                name = kProperty.getter.javaMethod!!.name
            )
        )
        additionalTypes.add(kProperty.returnType.javaClass.name)
        return super.propertyDataFetcherFactory(kClass, kProperty)
    }

    fun reflectMetadata(): List<ClassMetadata> {
        additionalTypes.filter { kClass -> supportedPackages.any { pkg -> kClass.startsWith(pkg) } }
            .forEach { kClass ->
                val existingMetadata = reflectMetadataMap[kClass]
                val javaClass = scanResult.loadClass(kClass, false)
                if (javaClass.isEnum && existingMetadata == null) {
                    val fields = javaClass.enumConstants.map { it as Enum<*> }.map { enumValue -> FieldMetadata(enumValue.name) }.sortedBy { it.name }
                    reflectMetadataMap[kClass] = MutableClassMetadata(name = kClass, fields = fields)
                }
            }

        additionalInputTypes.filter { kClass -> supportedPackages.any { pkg -> kClass.startsWith(pkg) } }
            .forEach { kClass ->
                val existingMetadata = reflectMetadataMap[kClass]
                val javaClass = scanResult.loadClass(kClass, false)
                if (existingMetadata == null) {
                    if (javaClass.isEnum) {
                        val fields = javaClass.enumConstants.map { it as Enum<*> }.map { enumValue -> FieldMetadata(enumValue.name) }.sortedBy { it.name }
                        reflectMetadataMap[kClass] = MutableClassMetadata(name = kClass, fields = fields)
                    } else {
                        reflectMetadataMap[kClass] = MutableClassMetadata(
                            name = kClass,
                            allPublicConstructors = true
                        )
                    }
                } else if (!javaClass.isEnum) {
                    existingMetadata.allPublicConstructors = true
                }
            }

        return reflectMetadataMap.values.map { it.toClassMetadata() }.sortedBy { it.name }
    }
}
