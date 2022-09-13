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

package com.expediagroup.graphql.generator.federation.data.queries.simple

import com.expediagroup.graphql.generator.federation.directives.ContactDirective

@ContactDirective(
    name = "My Team Name",
    url = "https://myteam.slack.com/archives/teams-chat-room-url",
    description = "send urgent issues to [#oncall](https://yourteam.slack.com/archives/oncall)."
)
class SimpleSchema
