package com.expediagroup.graphql.sample.directives

import com.expediagroup.graphql.annotations.GraphQLDirective

@GraphQLDirective(description = "This validates inputted string is equal to specified argument")
annotation class SpecificValueOnly(val value: String)
