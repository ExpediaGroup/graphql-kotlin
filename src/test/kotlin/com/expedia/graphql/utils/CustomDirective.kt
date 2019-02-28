package com.expedia.graphql.utils

import com.expedia.graphql.annotations.GraphQLDirective

@GraphQLDirective(name = "customName", description = "custom description")
annotation class CustomDirective(val customValue: String)
