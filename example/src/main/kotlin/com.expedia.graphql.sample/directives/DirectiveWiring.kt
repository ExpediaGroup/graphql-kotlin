package com.expedia.graphql.sample.directives

import com.google.common.base.CaseFormat
import graphql.schema.idl.SchemaDirectiveWiring
import graphql.schema.idl.SchemaDirectiveWiringEnvironment
import kotlin.reflect.KClass

interface DirectiveWiring : SchemaDirectiveWiring {

    fun isResponsible(environment: SchemaDirectiveWiringEnvironment<*>): Boolean

}

fun getDirectiveName(kClass: KClass<out Annotation>): String =
        CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, kClass.simpleName!!)