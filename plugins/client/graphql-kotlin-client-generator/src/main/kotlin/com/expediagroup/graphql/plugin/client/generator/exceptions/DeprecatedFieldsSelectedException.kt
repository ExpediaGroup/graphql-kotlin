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

/**
 * Exception thrown when query specifies deprecated fields but client configuration does not allow it.
 */
internal class DeprecatedFieldsSelectedException(operationName: String, field: String, type: String) :
    RuntimeException("Operation $operationName specifies deprecated field - $field in $type. Update your operation or update your configuration to allow usage of deprecated fields")
