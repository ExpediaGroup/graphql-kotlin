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
import graphql.Scalars
import graphql.introspection.Introspection.DirectiveLocation
import graphql.schema.GraphQLArgument
import graphql.schema.GraphQLList
import graphql.schema.GraphQLNonNull

const val LINK_SPEC_URL = "https://specs.apollo.dev/link/v1.0/"
const val FEDERATION_SPEC_URL = "https://specs.apollo.dev/federation/v2.3"

/**
 * ```graphql
 * directive @link(url: String!, import: [Import]) repeatable on SCHEMA
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
 *
 * @see <a href="https://specs.apollo.dev/link/v1.0/">@link specification</a>
 */
@Repeatable
@GraphQLDirective(
    name = LINK_DIRECTIVE_NAME,
    description = LINK_DIRECTIVE_DESCRIPTION,
    locations = [DirectiveLocation.SCHEMA]
)
annotation class LinkDirective(val url: String, val import: Array<String>)

internal const val LINK_DIRECTIVE_NAME = "link"
private const val LINK_DIRECTIVE_DESCRIPTION = "Links definitions within the document to external schemas."

internal val LINK_DIRECTIVE_TYPE: graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
    .name(LINK_DIRECTIVE_NAME)
    .description(LINK_DIRECTIVE_DESCRIPTION)
    .validLocations(DirectiveLocation.SCHEMA)
    .argument(
        GraphQLArgument.newArgument()
            .name("url")
            .type(GraphQLNonNull.nonNull(Scalars.GraphQLString))
    )
    .argument(
        GraphQLArgument
            .newArgument()
            .name("import")
            .type(GraphQLList.list(Scalars.GraphQLString))
    )
    .repeatable(true)
    .build()

internal fun appliedLinkDirective(url: String, imports: List<String> = emptyList()) = LINK_DIRECTIVE_TYPE.toAppliedDirective()
    .transform { appliedDirectiveBuilder ->
        LINK_DIRECTIVE_TYPE.getArgument("url")
            .toAppliedArgument()
            .transform { argumentBuilder ->
                argumentBuilder.valueProgrammatic(url)
            }
            .let {
                appliedDirectiveBuilder.argument(it)
            }

        if (imports.isNotEmpty()) {
            LINK_DIRECTIVE_TYPE.getArgument("import")
                .toAppliedArgument()
                .transform { argumentBuilder ->
                    argumentBuilder.valueProgrammatic(imports)
                }
                .let {
                    appliedDirectiveBuilder.argument(it)
                }
        }
    }
