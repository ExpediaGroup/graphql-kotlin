package com.expedia.graphql.test.utils

import com.expedia.graphql.annotations.GraphQLDirective

@GraphQLDirective(name = "customName", description = "custom description")
annotation class CustomDirective(val customValue: String)
