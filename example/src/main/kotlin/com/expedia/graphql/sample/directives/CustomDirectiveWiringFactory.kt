package com.expedia.graphql.sample.directives

import com.expedia.graphql.directives.KotlinDirectiveWiringFactory
import com.expedia.graphql.directives.KotlinSchemaDirectiveEnvironment
import com.google.common.base.CaseFormat
import graphql.schema.GraphQLCodeRegistry
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.idl.SchemaDirectiveWiring
import kotlin.reflect.KClass

class CustomDirectiveWiringFactory(codeRegistry: GraphQLCodeRegistry.Builder) : KotlinDirectiveWiringFactory(codeRegistry = codeRegistry, manualWiring = mapOf<String, SchemaDirectiveWiring>("lowercase" to LowercaseSchemaDirectiveWiring())) {

    private val stringEvalDirectiveWiring = StringEvalSchemaDirectiveWiring()
    private val caleOnlyDirectiveWiring = CakeOnlySchemaDirectiveWiring()

    override fun providesSchemaDirectiveWiring(env: KotlinSchemaDirectiveEnvironment<GraphQLDirectiveContainer>): Boolean =
        env.directive.name == getDirectiveName(StringEval::class) ||  env.directive.name == getDirectiveName(CakeOnly::class)

    override fun getSchemaDirectiveWiring(env: KotlinSchemaDirectiveEnvironment<GraphQLDirectiveContainer>): SchemaDirectiveWiring? = when {
        env.directive.name == getDirectiveName(StringEval::class) -> stringEvalDirectiveWiring
        env.directive.name == getDirectiveName(CakeOnly::class) -> caleOnlyDirectiveWiring
        else -> null
    }
}

internal fun getDirectiveName(kClass: KClass<out Annotation>): String =
    CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, kClass.simpleName!!)
