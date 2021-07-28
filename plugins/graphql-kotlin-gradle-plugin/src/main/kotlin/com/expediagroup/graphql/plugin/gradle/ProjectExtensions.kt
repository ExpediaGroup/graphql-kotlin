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
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.api.BaseVariant
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask
import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateTestClientTask
import org.gradle.api.Project

// isolating Android specific code that will be conditionally executed only on Android projects
// see https://github.com/gradle/gradle/issues/8411
internal fun configureAndroidCompileTasks(project: Project, clientGeneratingTasks: List<GraphQLGenerateClientTask>, testClientGeneratingTasks: List<GraphQLGenerateTestClientTask>) {
    val androidExtension = project.extensions.findByType(BaseExtension::class.java)

    val (variantCompileNames, testVariantCompileNames) = when (androidExtension) {
        is LibraryExtension -> androidExtension.libraryVariants.map { it.calculateCompileTaskName() } to findTestVariants(androidExtension).map { it.calculateCompileTaskName() }
        is AppExtension -> androidExtension.applicationVariants.map { it.calculateCompileTaskName() } to findTestVariants(androidExtension).map { it.calculateCompileTaskName() }
        else -> throw RuntimeException(
            "Unsupported configuration - unable to determine appropriate Android compile Kotlin task. graphql-kotlin plugin only supports Android libraries and applications"
        )
    }

    for (clientTask in clientGeneratingTasks) {
        androidExtension.sourceSets.findByName("main")?.java?.srcDir(clientTask.outputDirectory)
        for (variantCompileName in variantCompileNames) {
            project.tasks.named(variantCompileName).configure { compileTask ->
                compileTask.dependsOn(clientTask)
            }
        }
    }
    for (testClientTask in testClientGeneratingTasks) {
        androidExtension.sourceSets.findByName("test")?.java?.srcDir(testClientTask.outputDirectory)
        for (testVariantCompileName in testVariantCompileNames) {
            project.tasks.named(testVariantCompileName).configure { compileTask ->
                compileTask.dependsOn(testClientTask)
            }
        }
    }
}

private fun findTestVariants(extension: TestedExtension): Set<BaseVariant> =
    extension.testVariants + extension.unitTestVariants

private fun BaseVariant.calculateCompileTaskName() = "compile${this.name.capitalize()}Kotlin"
