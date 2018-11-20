package com.expedia.graphql.sample.directives

import com.expedia.graphql.annotations.GraphQLDirective

@GraphQLDirective(description = "This validates string input")
annotation class CakeOnly