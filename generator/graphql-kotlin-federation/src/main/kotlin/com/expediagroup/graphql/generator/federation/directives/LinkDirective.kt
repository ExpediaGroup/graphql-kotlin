/*
 * Copyright 2024 Expedia, Inc
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
import graphql.schema.GraphQLScalarType

const val APOLLO_SPEC_URL = "https://specs.apollo.dev"

const val LINK_SPEC = "link"
const val LINK_SPEC_LATEST_VERSION = "1.0"
const val LINK_SPEC_URL_PREFIX = "$APOLLO_SPEC_URL/$LINK_SPEC"
const val LINK_SPEC_LATEST_URL = "$LINK_SPEC_URL_PREFIX/v$LINK_SPEC_LATEST_VERSION"

const val FEDERATION_SPEC = "federation"
const val FEDERATION_SPEC_LATEST_VERSION = "2.6"
const val FEDERATION_SPEC_URL_PREFIX = "$APOLLO_SPEC_URL/$FEDERATION_SPEC"
const val FEDERATION_SPEC_LATEST_URL = "$FEDERATION_SPEC_URL_PREFIX/v$FEDERATION_SPEC_LATEST_VERSION"

/**
 * ```graphql
 * directive @link(url: String!, as: String, import: [Import]) repeatable on SCHEMA
 * ```
 *
 * The `@link` directive links definitions within the document to external schemas.
 *
 * External schemas are identified by their url, which should end with a specification name and version with the following format: `{NAME}/v{MAJOR}.{MINOR}`, e.g. `url = "https://specs.apollo.dev/federation/v2.5"`.
 *
 * External types are associated with the target specification by annotating it with `@LinkedSpec` meta annotation. External types defined in the specification will be automatically namespaced
 * (prefixed with `{NAME}__`) unless they are explicitly imported. While both custom namespace (`as`) and import arguments are optional, due to https://github.com/ExpediaGroup/graphql-kotlin/issues/1830
 * we currently always require those values to be explicitly provided.
 *
 * @param url external schema URL
 * @param as custom namespace, should default to the specification name specified in the url
 * @param import list of imported schema elements
 *
 * @see <a href="https://specs.apollo.dev/link/v1.0/">@link specification</a>
 */
@Repeatable
@GraphQLDirective(
    name = LINK_DIRECTIVE_NAME,
    description = LINK_DIRECTIVE_DESCRIPTION,
    locations = [DirectiveLocation.SCHEMA]
)
annotation class LinkDirective(val url: String, val `as`: String, val import: Array<LinkImport>)

internal const val LINK_DIRECTIVE_NAME = "link"
private const val LINK_DIRECTIVE_DESCRIPTION = "Links definitions within the document to external schemas."

internal fun linkDirectiveDefinition(importScalarType: GraphQLScalarType): graphql.schema.GraphQLDirective = graphql.schema.GraphQLDirective.newDirective()
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

internal fun graphql.schema.GraphQLDirective.toAppliedLinkDirective(url: String, namespace: String? = null, imports: List<String> = emptyList()) = this.toAppliedDirective()
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
