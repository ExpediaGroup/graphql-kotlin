package com.expedia.graphql.directives

import graphql.language.NamedNode
import graphql.language.NodeParentTree
import graphql.schema.DataFetcher
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldsContainer
import graphql.schema.GraphqlElementParentTree
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import graphql.schema.idl.TypeDefinitionRegistry

private const val NOT_IMPLEMENTED_REASON = "not implemented as it is not used by graphql-kotlin"

@Suppress("Detekt.NotImplementedDeclaration")
class KotlinSchemaDirectiveEnvironment<T : GraphQLDirectiveContainer>(
    private val element: T,
    val coordinates: FieldCoordinates,
    private val directive: GraphQLDirective,
    private val codeRegistry: GraphQLCodeRegistry.Builder
) : SchemaDirectiveWiringEnvironment<T> {

    override fun getElement(): T = element

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        throw NotImplementedError(NOT_IMPLEMENTED_REASON)

    override fun getDirective(): GraphQLDirective = directive

    override fun getDirective(directiveName: String?): GraphQLDirective =
        throw NotImplementedError(NOT_IMPLEMENTED_REASON)

    override fun containsDirective(directiveName: String?): Boolean =
        throw NotImplementedError(NOT_IMPLEMENTED_REASON)

    override fun getNodeParentTree(): NodeParentTree<NamedNode<NamedNode<*>>> =
        throw NotImplementedError(NOT_IMPLEMENTED_REASON)

    override fun getBuildContext(): MutableMap<String, Any> =
        throw NotImplementedError(NOT_IMPLEMENTED_REASON)

    override fun getDirectives(): MutableMap<String, GraphQLDirective> =
        throw NotImplementedError(NOT_IMPLEMENTED_REASON)

    override fun setFieldDataFetcher(newDataFetcher: DataFetcher<*>): GraphQLFieldDefinition =
        throw NotImplementedError(NOT_IMPLEMENTED_REASON)

    override fun getFieldDataFetcher(): DataFetcher<*> =
        throw NotImplementedError(NOT_IMPLEMENTED_REASON)

    override fun getFieldsContainer(): GraphQLFieldsContainer =
        throw NotImplementedError(NOT_IMPLEMENTED_REASON)

    override fun getRegistry(): TypeDefinitionRegistry =
        throw NotImplementedError(NOT_IMPLEMENTED_REASON)

    override fun getElementParentTree(): GraphqlElementParentTree =
        throw NotImplementedError(NOT_IMPLEMENTED_REASON)

    override fun getCodeRegistry(): GraphQLCodeRegistry.Builder = codeRegistry
}
