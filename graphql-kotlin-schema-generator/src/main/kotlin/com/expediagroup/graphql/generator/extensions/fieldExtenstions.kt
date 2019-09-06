package com.expediagroup.graphql.generator.extensions

import com.expediagroup.graphql.annotations.GraphQLDescription
import java.lang.reflect.Field

internal fun Field.getGraphQLDescription(): String? = this.getAnnotation(GraphQLDescription::class.java)?.value

internal fun Field.getDeprecationReason(): String? = this.getDeclaredAnnotation(Deprecated::class.java)?.getReason()
