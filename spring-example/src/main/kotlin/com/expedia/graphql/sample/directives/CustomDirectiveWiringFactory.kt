package com.expedia.graphql.sample.directives

import com.expedia.graphql.directives.KotlinDirectiveWiringFactory
import com.expedia.graphql.directives.KotlinSchemaDirectiveEnvironment
import com.expedia.graphql.directives.KotlinSchemaDirectiveWiring
import com.google.common.base.CaseFormat
import graphql.schema.GraphQLDirectiveContainer
import kotlin.reflect.KClass

class CustomDirectiveWiringFactory : KotlinDirectiveWiringFactory(manualWiring = mapOf<String, KotlinSchemaDirectiveWiring>("lowercase" to LowercaseSchemaDirectiveWiring())) {

    private val stringEvalDirectiveWiring = StringEvalSchemaDirectiveWiring()
    private val caleOnlyDirectiveWiring = SpecificValueOnlySchemaDirectiveWiring()

    override fun getSchemaDirectiveWiring(environment: KotlinSchemaDirectiveEnvironment<GraphQLDirectiveContainer>): KotlinSchemaDirectiveWiring? = when {
        environment.directive.name == getDirectiveName(StringEval::class) -> stringEvalDirectiveWiring
        environment.directive.name == getDirectiveName(SpecificValueOnly::class) -> caleOnlyDirectiveWiring
        else -> null
    }
}

internal fun getDirectiveName(kClass: KClass<out Annotation>): String =
    CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, kClass.simpleName!!)
