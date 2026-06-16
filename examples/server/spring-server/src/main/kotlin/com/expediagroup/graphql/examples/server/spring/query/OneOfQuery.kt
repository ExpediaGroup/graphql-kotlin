/*
 * Copyright 2026 Expedia, Inc
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

package com.expediagroup.graphql.examples.server.spring.query

import com.expediagroup.graphql.generator.annotations.GraphQLDescription
import com.expediagroup.graphql.generator.annotations.GraphQLOneOf
import com.expediagroup.graphql.generator.annotations.GraphQLOneOfField
import com.expediagroup.graphql.generator.annotations.GraphQLOneOfFieldType
import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class OneOfQuery : Query {

    @GraphQLDescription("Describes a content block supplied as a @oneOf input.")
    fun describeContentBlock(input: ContentBlockInput): String = when (input) {
        is ContentBlockInput.Paragraph -> "paragraph: ${input.text}"
        is ContentBlockInput.BlockQuote -> buildString {
            append("blockquote: ${input.value}")
            input.attribution?.let { append(" ($it)") }
            input.attributionUrl?.let { append(" <$it>") }
        }
        is ContentBlockInput.Image -> "image: ${input.altText} at ${input.url}"
    }

    @GraphQLDescription("Describes each content block supplied as a list of @oneOf inputs.")
    fun describeContentBlocks(input: List<ContentBlockInput>): List<String> =
        input.map { contentBlock -> describeContentBlock(contentBlock) }

    @GraphQLDescription("Describes how a user would be looked up from a scalar or object @oneOf input.")
    fun findUserBy(input: UserLookupInput): String = when (input) {
        is UserLookupInput.ById -> "user id=${input.id.value}"
        is UserLookupInput.ByEmail -> "user email=${input.email}"
        is UserLookupInput.ByCriteria -> "user criteria name=${input.name} address=${input.address}"
    }

    @GraphQLDescription("Describes a nested @oneOf lookup for either a user or an organization.")
    fun resolveEntity(input: EntityLookupInput): String = when (input) {
        is EntityLookupInput.User -> "user ${describeUserLookup(input.lookup)}"
        is EntityLookupInput.Organization -> "organization ${describeOrganizationLookup(input.lookup)}"
    }

    private fun describeUserLookup(input: UserLookupInput): String = when (input) {
        is UserLookupInput.ById -> "id=${input.id.value}"
        is UserLookupInput.ByEmail -> "email=${input.email}"
        is UserLookupInput.ByCriteria -> "criteria name=${input.name} address=${input.address}"
    }

    private fun describeOrganizationLookup(input: OrganizationLookupInput): String = when (input) {
        is OrganizationLookupInput.ById -> "id=${input.id.value}"
        is OrganizationLookupInput.BySlug -> "slug=${input.slug}"
    }
}

@GraphQLDescription("A content block input where exactly one block shape must be supplied.")
@GraphQLOneOf
sealed interface ContentBlockInput {

    @GraphQLDescription("Paragraph content supplied as a wrapped @oneOf object field.")
    @GraphQLOneOfField("paragraph")
    data class Paragraph(
        @param:GraphQLDescription("The paragraph text.")
        val text: String
    ) : ContentBlockInput

    @GraphQLDescription("Quoted content supplied as a wrapped @oneOf object field.")
    @GraphQLOneOfField("blockquote")
    data class BlockQuote(
        @param:GraphQLDescription("The quoted text.")
        val value: String,
        @param:GraphQLDescription("The optional source of the quote.")
        val attribution: String?,
        @param:GraphQLDescription("The optional URL for the quote source.")
        val attributionUrl: String?
    ) : ContentBlockInput

    @GraphQLDescription("Image content supplied as a wrapped @oneOf object field.")
    @GraphQLOneOfField("image")
    data class Image(
        @param:GraphQLDescription("The image URL.")
        val url: String,
        @param:GraphQLDescription("The image alt text.")
        val altText: String
    ) : ContentBlockInput
}

@GraphQLDescription("A user lookup input where exactly one lookup strategy must be supplied.")
@GraphQLOneOf
sealed interface UserLookupInput {

    @GraphQLDescription("Lookup a user by ID using an unwrapped @oneOf scalar field.")
    @GraphQLOneOfField("id", GraphQLOneOfFieldType.UNWRAPPED)
    data class ById(
        @param:GraphQLDescription("The user ID.")
        val id: ID
    ) : UserLookupInput

    @GraphQLDescription("Lookup a user by email using an unwrapped @oneOf scalar field.")
    @GraphQLOneOfField("email", GraphQLOneOfFieldType.UNWRAPPED)
    data class ByEmail(
        @param:GraphQLDescription("The user's email address.")
        val email: String
    ) : UserLookupInput

    @GraphQLDescription("Lookup a user by criteria using a wrapped @oneOf object field.")
    @GraphQLOneOfField("criteria")
    data class ByCriteria(
        @param:GraphQLDescription("The optional display name to match.")
        val name: String?,
        @param:GraphQLDescription("The optional mailing address to match.")
        val address: String?
    ) : UserLookupInput
}

@GraphQLDescription("An entity lookup input where exactly one entity type must be supplied.")
@GraphQLOneOf
sealed interface EntityLookupInput {

    @GraphQLDescription("Lookup a user using a nested @oneOf selector.")
    @GraphQLOneOfField("user", GraphQLOneOfFieldType.UNWRAPPED)
    data class User(
        @param:GraphQLDescription("The nested user lookup selector.")
        val lookup: UserLookupInput
    ) : EntityLookupInput

    @GraphQLDescription("Lookup an organization using a nested @oneOf selector.")
    @GraphQLOneOfField("organization", GraphQLOneOfFieldType.UNWRAPPED)
    data class Organization(
        @param:GraphQLDescription("The nested organization lookup selector.")
        val lookup: OrganizationLookupInput
    ) : EntityLookupInput
}

@GraphQLDescription("An organization lookup input where exactly one lookup strategy must be supplied.")
@GraphQLOneOf
sealed interface OrganizationLookupInput {

    @GraphQLDescription("Lookup an organization by ID using an unwrapped @oneOf scalar field.")
    @GraphQLOneOfField("id", GraphQLOneOfFieldType.UNWRAPPED)
    data class ById(
        @param:GraphQLDescription("The organization ID.")
        val id: ID
    ) : OrganizationLookupInput

    @GraphQLDescription("Lookup an organization by slug using an unwrapped @oneOf scalar field.")
    @GraphQLOneOfField("slug", GraphQLOneOfFieldType.UNWRAPPED)
    data class BySlug(
        @param:GraphQLDescription("The organization slug.")
        val slug: String
    ) : OrganizationLookupInput
}
