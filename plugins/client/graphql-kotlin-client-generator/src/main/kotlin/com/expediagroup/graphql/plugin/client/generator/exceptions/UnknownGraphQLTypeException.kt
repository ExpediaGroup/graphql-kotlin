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

package com.expediagroup.graphql.plugin.client.generator.exceptions

import graphql.language.Node

/**
 * This exception should never be thrown. It is used to enforce type safety of type name generation operations. Exception
 * is used to mark unreachable code execution branches that ensures non null response from all valid branches.
 */
internal class UnknownGraphQLTypeException(graphQLType: Node<*>) :
    RuntimeException("Client generation failure - attempting to generate code for unsupported GraphQL type $graphQLType")
