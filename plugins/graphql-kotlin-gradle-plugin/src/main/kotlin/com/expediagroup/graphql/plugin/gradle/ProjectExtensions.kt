/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.plugin.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.api.BaseVariant
import org.gradle.api.Project

// have to be outside plugin class due to https://github.com/gradle/gradle/issues/8411
internal fun Project.findKotlinCompileTasks(): Pair<List<String>, List<String>> {
    val extension = this.extensions.findByName("android")
    return if (extension == null) {
        // default to JVM
        listOf("compileKotlin") to listOf("compileTestKotlin")
    } else {
        when (extension) {
            is LibraryExtension -> extension.libraryVariants.map { calculateCompileTaskName(it) } to findTestVariantCompileTasks(extension)
            is AppExtension -> extension.applicationVariants.map { calculateCompileTaskName(it) } to findTestVariantCompileTasks(extension)
            else -> throw RuntimeException(
                "Unsupported configuration - unable to determine appropriate compile Kotlin task. graphql-kotlin plugin only supports default JVM builds, Android libraries and applications"
            )
        }
    }
}

private fun findTestVariantCompileTasks(extension: TestedExtension) =
    extension.testVariants.map { calculateCompileTaskName(it) } + extension.unitTestVariants.map { calculateCompileTaskName(it) }

private fun calculateCompileTaskName(variant: BaseVariant) = "compile${variant.name.capitalize()}Kotlin"
