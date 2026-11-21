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

@file:OptIn(ExperimentalSemanticIntrospection::class)

package com.expediagroup.graphql.generator.semantic

import com.expediagroup.graphql.generator.extensions.getOrElse
import graphql.ErrorClassification
import graphql.ErrorType
import graphql.GraphQLError
import graphql.GraphqlErrorBuilder
import graphql.Scalars.GraphQLFloat
import graphql.Scalars.GraphQLInt
import graphql.Scalars.GraphQLString
import graphql.execution.DataFetcherResult
import graphql.introspection.Introspection
import graphql.language.FieldDefinition
import graphql.language.ListType
import graphql.language.NonNullType
import graphql.language.TypeName
import graphql.schema.DataFetcher
import graphql.schema.DataFetchingEnvironment
import graphql.schema.FieldCoordinates.coordinates
import graphql.schema.GraphQLArgument.newArgument
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLEnumType
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLFieldDefinition.newFieldDefinition
import graphql.schema.GraphQLFieldsContainer
import graphql.schema.GraphQLInputFieldsContainer
import graphql.schema.GraphQLList.list
import graphql.schema.GraphQLNonNull.nonNull
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLObjectType.newObject
import graphql.schema.GraphQLSchema
import graphql.schema.GraphQLSchemaElement
import graphql.schema.GraphQLTypeVisitorStub
import graphql.schema.SchemaTransformer
import graphql.util.TraversalControl
import graphql.util.TraverserContext
import graphql.util.TreeTransformerUtil.changeNode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future
import java.util.function.Predicate
import kotlin.coroutines.EmptyCoroutineContext

/** Name of the search field added to the query type. The RFC's `__search`: graphql-java reserves `__` names. */
@ExperimentalSemanticIntrospection
const val SEMANTIC_SEARCH_FIELD = "_search"

/** Name of the search result type. The RFC's `__SearchResult`. */
@ExperimentalSemanticIntrospection
const val SEMANTIC_SEARCH_RESULT_TYPE = "_SearchResult"

private const val DEFAULT_FIRST = 10

// Marks the generated field's AST, so printing recognizes it from schema metadata alone.
private const val GENERATED = "com.expediagroup.graphql.semanticIntrospection"

// Schema coordinates: `Type`, `Type.member`, `Type.field(arg:)`, `@directive`, `@directive(arg:)`. One optional space
// before `)` matches the RFC's own example, `Mutation.createPost(input: )`.
private val COORDINATE = Regex("""(@?)([_A-Za-z]\w*)(?:\.([_A-Za-z]\w*))?(?:\(([_A-Za-z]\w*): ?\))?""")

// Asks the visibility in effect, so decorating visibilities that hide introspection are caught too.
private fun GraphQLSchema.exposesIntrospection() = codeRegistry.fieldVisibility.getFieldDefinitions(introspectionSchemaType).isNotEmpty()

/**
 * Adds `Query._search` from the Semantic Introspection RFC, resolved by [resolver]:
 *
 * ```graphql
 * type Query { _search(query: String!, first: Int! = 10, minScore: Float): [_SearchResult!] }
 * type _SearchResult { coordinate: String!, pathsToRoot: [[String!]!]!, score: Float }
 * ```
 *
 * Results are coordinates only; clients read definitions through standard introspection. Apply it to the final
 * schema: after federation, `_service` SDL never includes it, and [com.expediagroup.graphql.generator.extensions.print]
 * leaves it out of printed SDL.
 *
 * @throws IllegalArgumentException if the schema was built with introspection disabled
 * @throws IllegalStateException if the schema already has `_search` or `_SearchResult`
 */
@ExperimentalSemanticIntrospection
fun GraphQLSchema.withSemanticIntrospection(resolver: SemanticSearchResolver): GraphQLSchema {
    require(exposesIntrospection()) {
        "Semantic introspection needs introspection enabled"
    }
    check(queryType.getFieldDefinition(SEMANTIC_SEARCH_FIELD) == null && getType(SEMANTIC_SEARCH_RESULT_TYPE) == null) {
        "Semantic introspection needs $SEMANTIC_SEARCH_FIELD and $SEMANTIC_SEARCH_RESULT_TYPE to be free; " +
            "apply it once, through a SemanticSearchResolver bean or withSemanticIntrospection, not both"
    }

    val result = newObject()
        .name(SEMANTIC_SEARCH_RESULT_TYPE)
        .description("A schema member matched by $SEMANTIC_SEARCH_FIELD.")
        .field(newFieldDefinition().name("coordinate").description("Schema coordinate, for example `Type`, `Type.field`, `Type.field(arg:)` or `@directive`.").type(nonNull(GraphQLString)))
        .field(newFieldDefinition().name("pathsToRoot").description("Paths from a root field, as field coordinates.").type(nonNull(list(nonNull(list(nonNull(GraphQLString)))))))
        .field(newFieldDefinition().name("score").description("Relevance from 0 to 1.").type(GraphQLFloat))
        .build()
    val search = newFieldDefinition()
        .name(SEMANTIC_SEARCH_FIELD)
        .description("Searches the schema for members matching a natural language query, best match first (Semantic Introspection RFC).")
        .argument(newArgument().name("query").type(nonNull(GraphQLString)))
        .argument(newArgument().name("first").type(nonNull(GraphQLInt)).defaultValueProgrammatic(DEFAULT_FIRST))
        .argument(newArgument().name("minScore").type(GraphQLFloat))
        .type(list(nonNull(result)))
        .definition(
            FieldDefinition.newFieldDefinition()
                .name(SEMANTIC_SEARCH_FIELD)
                .type(ListType(NonNullType(TypeName(SEMANTIC_SEARCH_RESULT_TYPE))))
                .additionalData(GENERATED, "true")
                .build()
        )
        .build()

    val queryName = queryType.name
    return SchemaTransformer.transformSchema(
        this,
        object : GraphQLTypeVisitorStub() {
            override fun visitGraphQLObjectType(node: GraphQLObjectType, context: TraverserContext<GraphQLSchemaElement>): TraversalControl {
                if (node.name != queryName) return TraversalControl.CONTINUE
                // The transformer's own registry: the rebuilt schema is assembled from it.
                val registry = context.getVarFromParents(GraphQLCodeRegistry.Builder::class.java)
                registry.dataFetcher(coordinates(queryName, SEMANTIC_SEARCH_FIELD), SemanticSearchDataFetcher(resolver))
                registry.dataFetcher(coordinates(SEMANTIC_SEARCH_RESULT_TYPE, "coordinate"), DataFetcher { it.getSource<SemanticSearchResult>()!!.coordinate })
                registry.dataFetcher(coordinates(SEMANTIC_SEARCH_RESULT_TYPE, "pathsToRoot"), DataFetcher { it.getSource<SemanticSearchResult>()!!.pathsToRoot })
                registry.dataFetcher(coordinates(SEMANTIC_SEARCH_RESULT_TYPE, "score"), DataFetcher { it.getSource<SemanticSearchResult>()!!.score })
                return changeNode(context, node.transform { it.field(search) })
            }
        }
    ) { builder ->
        // The rebuild drops the schema's own SDL definitions.
        builder.definition(definition).extensionDefinitions(extensionDefinitions)
    }
}

/**
 * Printed SDL leaves out what [withSemanticIntrospection] added: like introspection, `_search` belongs to the running
 * service, not to schemas composed or stitched from SDL. An application's own `_search` still prints.
 */
internal fun semanticIntrospectionFilter(schema: GraphQLSchema): Predicate<GraphQLSchemaElement>? {
    val search = schema.queryType.getFieldDefinition(SEMANTIC_SEARCH_FIELD)
    if (search?.definition?.additionalData?.get(GENERATED) != "true") return null
    val result = schema.getType(SEMANTIC_SEARCH_RESULT_TYPE)
    return Predicate { it !== search && it !== result }
}

internal class SemanticSearchDataFetcher(private val resolver: SemanticSearchResolver) : DataFetcher<Any?> {
    override fun get(environment: DataFetchingEnvironment): Any? {
        // Mirrors graphql-java's checks for __schema and __type, which don't cover other fields, plus the executing
        // schema's visibility, which may hide introspection after _search was added.
        val disabled = !Introspection.isEnabledJvmWide() ||
            environment.graphQlContext.getBoolean(Introspection.INTROSPECTION_DISABLED, false) ||
            !environment.graphQLSchema.exposesIntrospection()
        if (disabled) {
            return fieldError(environment, "Introspection is disabled", ErrorClassification.errorClassification("IntrospectionDisabled"))
        }
        val request = SemanticSearchRequest(
            // Both non-null in the schema; first has a default.
            query = environment.getArgument<String>("query")!!,
            first = environment.getArgument<Int>("first")!!,
            minScore = environment.getArgument<Double>("minScore")
        )
        if (request.first < 1) return fieldError(environment, "first must be at least 1", ErrorType.ValidationError)
        val schema = environment.graphQLSchema
        // Same scope convention as FunctionDataFetcher.
        return environment.graphQlContext.getOrElse { CoroutineScope(EmptyCoroutineContext) }.future<Any?> {
            val results = try {
                resolver.search(environment, request)
            } catch (exception: CancellationException) {
                throw exception
            } catch (@Suppress("TooGenericExceptionCaught") exception: Exception) {
                // A GraphQLError is meant for the client as is. Converted here, it doesn't fail the request's scope.
                if (exception !is GraphQLError) throw exception
                return@future DataFetcherResult.newResult<Any>().error(exception).build()
            }
            checkPage(SchemaCoordinates(schema), request, results)
        }
    }

    private fun fieldError(environment: DataFetchingEnvironment, message: String, type: ErrorClassification): DataFetcherResult<Any> {
        // Explicit star type: inferring GraphqlErrorBuilder's recursive generic overflows the Kotlin 2.3 compiler.
        val error: GraphqlErrorBuilder<*> = GraphqlErrorBuilder.newError(environment)
        return DataFetcherResult.newResult<Any>().error(error.message(message).errorType(type).build()).build()
    }

    /**
     * Fails invalid results rather than trimming them; the messages never name a coordinate,
     * which may be hidden. Paths through hidden fields are dropped: the match is still reachable another way.
     */
    private fun checkPage(coordinates: SchemaCoordinates, request: SemanticSearchRequest, results: List<SemanticSearchResult>): List<SemanticSearchResult> {
        check(results.size <= request.first) { "Semantic search resolver returned more results than first" }
        return results.map { result ->
            check(coordinates.isMember(result.coordinate)) { "Semantic search resolver returned an unknown or hidden coordinate" }
            val minScore = request.minScore
            check(minScore == null || (result.score != null && result.score >= minScore)) { "Semantic search resolver returned a score below minScore" }
            val paths = result.pathsToRoot.filter { path -> path.isNotEmpty() && path.all(coordinates::isField) }
            if (paths.size == result.pathsToRoot.size) result else SemanticSearchResult(result.coordinate, paths, result.score)
        }
    }
}

/** Visible schema coordinates, with output fields looked up once per type per request. */
private class SchemaCoordinates(private val schema: GraphQLSchema) {
    private val fieldsByType = HashMap<String, Map<String, GraphQLFieldDefinition>>()

    fun isMember(coordinate: String): Boolean {
        val (directive, name, member, argument) = COORDINATE.matchEntire(coordinate)?.destructured ?: return false
        return when {
            directive.isNotEmpty() -> member.isEmpty() && schema.getDirective(name)?.let { argument.isEmpty() || it.getArgument(argument) != null } == true
            member.isEmpty() -> argument.isEmpty() && !isHidden(name) && schema.getType(name) != null
            argument.isNotEmpty() -> field(name, member)?.getArgument(argument) != null
            else -> field(name, member) != null || isInputOrEnumMember(name, member)
        }
    }

    /** `Type.field` on an object or interface type, the only form a path step takes. */
    fun isField(coordinate: String): Boolean {
        val parts = coordinate.split('.')
        return parts.size == 2 && field(parts[0], parts[1]) != null
    }

    private fun field(type: String, field: String): GraphQLFieldDefinition? = fieldsByType.getOrPut(type) { visibleFields(type) }[field]

    private fun visibleFields(type: String): Map<String, GraphQLFieldDefinition> {
        val container = schema.getType(type) as? GraphQLFieldsContainer
        if (container == null || isHidden(type)) return emptyMap()
        return schema.codeRegistry.fieldVisibility.getFieldDefinitions(container)
            .filterNot { type == schema.queryType.name && it.name == SEMANTIC_SEARCH_FIELD }
            .associateBy(GraphQLFieldDefinition::getName)
    }

    private fun isInputOrEnumMember(type: String, member: String): Boolean = when (val named = schema.getType(type)?.takeUnless { isHidden(type) }) {
        is GraphQLInputFieldsContainer -> schema.codeRegistry.fieldVisibility.getFieldDefinitions(named).any { it.name == member }
        is GraphQLEnumType -> named.getValue(member) != null
        else -> false
    }

    private fun isHidden(type: String) = Introspection.isIntrospectionTypes(type) || type == SEMANTIC_SEARCH_RESULT_TYPE
}
