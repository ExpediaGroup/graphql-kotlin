/*
 * Copyright 2023 Expedia, Inc
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
import com.expediagroup.graphql.generator.internal.types.DEFAULT_DIRECTIVE_STRING_VALUE
import graphql.Scalars
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull
import graphql.schema.GraphQLScalarType

const val LINK_SPEC = "link"
const val LINK_SPEC_URL = "https://specs.apollo.dev/link"
const val LINK_SPEC_LATEST_VERSION = "1.0"
const val FEDERATION_SPEC = "federation"
const val FEDERATION_SPEC_URL = "https://specs.apollo.dev/federation"
const val FEDERATION_LATEST_VERSION = "2.5"

/**
 * ```graphql
 * directive @link(url: String!, as: String, import: [Import]) repeatable on SCHEMA
 * ```
 *
 * The `@link` directive links definitions within the document to external schemas.
 *
 * External schemas are identified by their url, which optionally ends with a name and version with the following format: `{NAME}/v{MAJOR}.{MINOR}`
 *
 * By default, external types should be namespaced (prefixed with namespace__, e.g. key directive should be namespaced as federation__key) unless they are explicitly imported. `graphql-kotlin`
 * automatically imports ALL federation directives to avoid the need for namespacing.
 *
 * >NOTE: We currently DO NOT support full `@link` directive capability as it requires support for namespacing and renaming imports. This functionality may be added in the future releases. See `@link`
 * specification for details.
 *
 * @param url external schema URL
 * @param import list of imported schema elements
 * @param as import namespace
 *
 * @see <a href="https://specs.apollo.dev/link/v1.0/">@link specification</a>
 */
@Repeatable
@GraphQLDirective(
    name = LINK_DIRECTIVE_NAME,
    description = LINK_DIRECTIVE_DESCRIPTION,
    locations = [DirectiveLocation.SCHEMA]
)
annotation class LinkDirective(val url: String, val `as`: String = DEFAULT_DIRECTIVE_STRING_VALUE, val import: Array<LinkImport>)

internal const val LINK_DIRECTIVE_NAME = "link"
private const val LINK_DIRECTIVE_DESCRIPTION = "Links definitions within the document to external schemas."

internal fun linkDirectiveType(importScalarType: GraphQLScalarType): graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(LINK_DIRECTIVE_NAME)
    .description(LINK_DIRECTIVE_DESCRIPTION)
    .validLocations(DirectiveLocation.SCHEMA)
    .argument(
        GraphQLArgument.newArgument()
            .name("url")
            .type(GraphQLNonNull.nonNull(Scalars.GraphQLString))
    )
    .argument(
        GraphQLArgument.newArgument()
            .name("as")
            .type(Scalars.GraphQLString)
    )
    .argument(
        GraphQLArgument
            .newArgument()
            .name("import")
            .type(GraphQLList.list(importScalarType))
    )
    .repeatable(true)
    .build()

internal fun graphql.schema.GraphQLDirective.toAppliedLinkDirective(url: String, namespace: String?, imports: List<String> = emptyList()) = this.toAppliedDirective()
    .transform { appliedDirectiveBuilder ->
        this.getArgument("url")
            .toAppliedArgument()
            .transform { argumentBuilder ->
                argumentBuilder.valueProgrammatic(url)
            }
            .let {
                appliedDirectiveBuilder.argument(it)
            }

        if (!namespace.isNullOrBlank()) {
            this.getArgument("as")
                .toAppliedArgument()
                .transform { argumentBuilder ->
                    argumentBuilder.valueProgrammatic(namespace)
                }
                .let {
                    appliedDirectiveBuilder.argument(it)
                }
        }

        if (imports.isNotEmpty()) {
            this.getArgument("import")
                .toAppliedArgument()
                .transform { argumentBuilder ->
                    argumentBuilder.valueProgrammatic(imports)
                }
                .let {
                    appliedDirectiveBuilder.argument(it)
                }
        }
    }
