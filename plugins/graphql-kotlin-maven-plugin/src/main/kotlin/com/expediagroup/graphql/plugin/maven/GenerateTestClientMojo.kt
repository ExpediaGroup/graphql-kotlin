/*
 * Copyright 2020 Expedia, Inc
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

package com.expediagroup.graphql.plugin.maven

import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.project.MavenProject
import java.io.File

/**
 * Generate GraphQL Kotlin client data model based on the provided GraphQL schema and target queries. Upon successful client generation
 * project test sources will be updated with newly generated client code.
 */
@Mojo(name = "generate-test-client", defaultPhase = LifecyclePhase.GENERATE_TEST_SOURCES)
class GenerateTestClientMojo : GenerateClientAbstractMojo() {

    /**
     * Directory file containing GraphQL queries, defaults to `src/test/resources`. Instead of specifying a directory you can
     * also specify list of query file by using [queryFiles] property instead.
     */
    @Parameter(defaultValue = "\${project.basedir}/src/test/resources", name = "queryFileDirectory")
    override lateinit var queryFileDirectory: File

    /**
     * Target directory where to store generated files, defaults to `target/generated-test-sources/graphql`.
     */
    @Parameter(defaultValue = "\${project.build.directory}/generated-test-sources/graphql", name = "outputDirectory")
    override lateinit var outputDirectory: File

    override fun configureProjectWithGeneratedSources(mavenProject: MavenProject, generatedSourcesDirectory: File) {
        mavenProject.addTestCompileSourceRoot(generatedSourcesDirectory.path)
    }
}
