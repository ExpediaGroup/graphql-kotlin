package com.expedia.graphql.sample.directives

import com.expedia.graphql.annotations.GraphQLDirective

@GraphQLDirective(description = "This validates inputted string is equal to cake")
annotation class CakeOnly