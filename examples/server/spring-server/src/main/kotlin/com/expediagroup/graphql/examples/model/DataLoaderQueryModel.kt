package com.expediagroup.graphql.examples.model

import com.expediagroup.graphql.generator.annotations.GraphQLIgnore

data class Employee(
    val name: String,
    @GraphQLIgnore
    val companyId: Int
) {
    lateinit var company: Company
}

data class Company(val id: Int, val name: String)
