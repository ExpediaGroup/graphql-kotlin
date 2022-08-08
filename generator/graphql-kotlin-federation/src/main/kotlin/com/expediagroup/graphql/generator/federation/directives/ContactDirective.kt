/*
 * Copyright 2022 Expedia, Inc
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

package com.expediagroup.graphql.generator.federation.directives

import com.expediagroup.graphql.generator.annotations.GraphQLDirective
import graphql.introspection.Introspection.DirectiveLocation

/**
 * ```graphql
 * directive @contact(
 *   "Contact title of the subgraph owner"
 *   name: String!
 *   "URL where the subgraph's owner can be reached"
 *   url: String
 *   "Other relevant notes can be included here; supports markdown links"
 *   description: String
 * ) on SCHEMA
 * ```
 *
 * Contact schema directive can be used to provide team contact information to your subgraph schema. This information is automatically parsed and displayed by Apollo Studio.
 *
 * Example usage on schema class:
 * ```kotlin
 * @ContactDirective(
 *   name = "My Team Name",
 *   url = "https://myteam.slack.com/archives/teams-chat-room-url",
 *   description = "send urgent issues to [#oncall](https://yourteam.slack.com/archives/oncall)."
 * )
 * class MySchema
 * ```
 *
 * @param name subgraph owner name
 * @param url optional URL where the subgraph's owner can be reached
 * @param description optional additional information about contacting the owners, can include Markdown links
 *
 * @see <a href="https://www.apollographql.com/docs/studio/federated-graphs/#subgraph-contact-info">Subgraph Contact Info</a>
 */
@GraphQLDirective(
    name = CONTACT_DIRECTIVE_NAME,
    description = CONTACT_DIRECTIVE_DESCRIPTION,
    locations = [DirectiveLocation.SCHEMA]
)
annotation class ContactDirective(
    /** Contact title of the subgraph owner */
    val name: String,
    /** URL where the subgraph's owner can be reached */
    val url: String = "",
    /** Other relevant notes can be included here; supports Markdown links */
    val description: String = ""
)

internal const val CONTACT_DIRECTIVE_NAME = "contact"
private const val CONTACT_DIRECTIVE_DESCRIPTION = "Provides contact information of the owner responsible for this subgraph schema."
