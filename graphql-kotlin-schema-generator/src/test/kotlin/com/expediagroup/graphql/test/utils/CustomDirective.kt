package com.expediagroup.graphql.test.utils

import com.expediagroup.graphql.annotations.GraphQLDirective

@GraphQLDirective(name = "customName", description = "custom description")
annotation class CustomDirective(val customValue: String)
