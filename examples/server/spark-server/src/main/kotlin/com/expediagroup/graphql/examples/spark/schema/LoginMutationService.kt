/**
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
package com.expediagroup.graphql.examples.spark.schema

import com.expediagroup.graphql.examples.spark.schema.models.User

data class AuthPayload(val token: String? = null, val user: User? = null)

class LoginMutationService {
    suspend fun login(email: String, password: String, aliasUUID: String?): AuthPayload {
        val token = "fake-token"
        val user = User(
            email = "fake@site.com",
            firstName = "Someone",
            lastName = "You Don't know",
            universityId = 4
        )
        return AuthPayload(token, user)
    }
}
