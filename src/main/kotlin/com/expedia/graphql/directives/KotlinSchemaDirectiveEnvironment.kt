package com.expedia.graphql.directives

import graphql.language.NamedNode
import graphql.language.NodeParentTree
import graphql.schema.FieldCoordinates
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldsContainer
import graphql.schema.GraphqlElementParentTree
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import graphql.schema.idl.TypeDefinitionRegistry

class KotlinSchemaDirectiveEnvironment<T : GraphQLDirectiveContainer>(
    private val element: T,
    val coordinates: FieldCoordinates,
    private val directive: GraphQLDirective,
    private val codeRegistry: GraphQLCodeRegistry.Builder
): SchemaDirectiveWiringEnvironment<T> {

    override fun getElement(): T = element

    override fun getFieldDefinition(): GraphQLFieldDefinition =
        throw NotImplementedError("not implemented as it is not used by graphql-kotlin")

    override fun getDirective(): GraphQLDirective = directive

    override fun getNodeParentTree(): NodeParentTree<NamedNode<NamedNode<*>>> =
        throw NotImplementedError("not implemented as it is not used by graphql-kotlin")

    override fun getFieldsContainer(): GraphQLFieldsContainer =
        throw NotImplementedError("not implemented as it is not used by graphql-kotlin")

    override fun getRegistry(): TypeDefinitionRegistry =
        throw NotImplementedError("not implemented as it is not used by graphql-kotlin")

    override fun getElementParentTree(): GraphqlElementParentTree =
        throw NotImplementedError("not implemented as it is not used by graphql-kotlin")

    override fun getCodeRegistry(): GraphQLCodeRegistry.Builder = codeRegistry

    override fun getBuildContext(): MutableMap<String, Any> =
        throw NotImplementedError("not implemented as it is not used by graphql-kotlin")
}
