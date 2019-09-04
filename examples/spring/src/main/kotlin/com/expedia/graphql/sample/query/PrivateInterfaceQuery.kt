package com.expedia.graphql.sample.query

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.sample.model.HidesInheritance
import org.springframework.stereotype.Component

@Component
class PrivateInterfaceQuery : Query {

    @GraphQLDescription("this query returns class implementing private interface which is not exposed in the schema")
    fun queryForObjectWithPrivateInterface(): HidesInheritance = HidesInheritance(id = 123)
}
