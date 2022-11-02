package com.expediagroup.graphql.plugin.gradle.config

import java.io.Serializable

/**
 * Configure options for parsing GraphQL queries and schema definition language documents. Settings
 * here override the defaults set by GraphQL Java.
 */
data class GraphQLParserOptions(
    /** Modify the maximum number of tokens read to prevent processing extremely large queries */
    var maxTokens: Int? = 15000,
    /** Modify the maximum number of whitespace tokens read to prevent processing extremely large queries */
    var maxWhitespaceTokens: Int? = 200000,
    /** Memory usage is significantly reduced by not capturing ignored characters, especially in SDL parsing. */
    var captureIgnoredChars: Boolean? = false,
    /** Single-line comments do not have any semantic meaning in GraphQL source documents and can be ignored */
    var captureLineComments: Boolean? = true,
    /** Memory usage is reduced by not setting SourceLocations on AST nodes, especially in SDL parsing. */
    var captureSourceLocation: Boolean? = true
) : Serializable
