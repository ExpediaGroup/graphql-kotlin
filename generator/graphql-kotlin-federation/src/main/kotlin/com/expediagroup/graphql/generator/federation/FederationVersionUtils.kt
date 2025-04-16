/*
 * Copyright 2025 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation

/**
 * Checks if the federation version from the URL meets or exceeds the specified version.
 *
 * @param federationUrl The federation specification URL (e.g., "https://specs.apollo.dev/federation/v2.7")
 * @param major The major version to check against
 * @param minor The minor version to check against
 * @return True if the URL's version is at least the specified major.minor version
 */
internal fun isFederationVersionAtLeast(federationUrl: String, major: Int, minor: Int): Boolean {
    val versionRegex = """.*?/v?(\d+)\.(\d+).*""".toRegex()
    val matchResult = versionRegex.find(federationUrl)

    return if (matchResult != null) {
        val (majorStr, minorStr) = matchResult.destructured
        val fedMajor = majorStr.toIntOrNull() ?: 0
        val fedMinor = minorStr.toIntOrNull() ?: 0

        fedMajor > major || (fedMajor == major && fedMinor >= minor)
    } else {
        false
    }
}
