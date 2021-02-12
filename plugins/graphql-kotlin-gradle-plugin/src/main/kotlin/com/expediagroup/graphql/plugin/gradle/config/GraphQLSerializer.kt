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

package com.expediagroup.graphql.plugin.gradle.config

/**
 * JSON serializer that will be used to generate the data classes.
 *
 * In order to limit the amount of plugin dependencies, we cannot use client-generator GraphQLSerializer enum directly as it is declared
 * as a compileOnly dependency (which will be available on the worker classpath only). We need to re-define the same object here so it will
 * be accessible from build file configurations.
 */
enum class GraphQLSerializer {
    KOTLINX,
    JACKSON
}
