/*
 * Compatibility shim for graphql-java 26.0.beta-2.
 *
 * graphql-java 25.x provided graphql.schema.idl.DirectiveInfo with
 * GRAPHQL_SPECIFICATION_DIRECTIVE_MAP and isGraphqlSpecifiedDirective().
 * These were removed in graphql-java 26 and replaced by graphql.Directives.
 *
 * federation-graphql-java-support 6.0.0 (built against gj25) still references
 * DirectiveInfo at runtime in ServiceSDLPrinter. This shim bridges the gap
 * until a gj26-compatible federation-graphql-java-support release is available.
 *
 * TODO: Remove this shim when federation-graphql-java-support releases a version
 *       built against graphql-java 26+.
 */

package graphql.schema.idl

import graphql.Directives
import graphql.schema.GraphQLDirective

/**
 * Static fields and methods accessed via JVM bytecode by
 * com.apollographql.federation.graphqljava.printer.ServiceSDLPrinter.
 */
object DirectiveInfo {
    /**
     * Map of specification directive names to their [GraphQLDirective] definitions.
     * Equivalent to the removed graphql-java 25 DirectiveInfo.GRAPHQL_SPECIFICATION_DIRECTIVE_MAP.
     */
    @JvmField
    val GRAPHQL_SPECIFICATION_DIRECTIVE_MAP: Map<String, GraphQLDirective> = Directives.BUILT_IN_DIRECTIVES_MAP

    /**
     * Checks whether a given directive is a GraphQL specification directive.
     * Equivalent to the removed graphql-java 25 DirectiveInfo.isGraphqlSpecifiedDirective().
     */
    @JvmStatic
    fun isGraphqlSpecifiedDirective(directive: GraphQLDirective): Boolean =
        Directives.isBuiltInDirective(directive)
}
