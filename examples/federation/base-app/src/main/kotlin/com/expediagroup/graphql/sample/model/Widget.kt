package com.expediagroup.graphql.sample.model

import com.expediagroup.graphql.annotations.GraphQLDescription
import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.directives.KeyDirective

@KeyDirective(fields = FieldSet("id"))
@GraphQLDescription("A useful widget")
data class Widget(
    val id: Int,
    @GraphQLDescription("The widget's value that can be null")
    val value: Int? = null
) {

    @GraphQLDescription("returns original value multiplied by target OR null if original value was null")
    fun multiplyValueBy(multiplier: Int) = value?.times(multiplier)
}
