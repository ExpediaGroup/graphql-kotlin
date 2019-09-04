package com.expedia.graphql.sample.model

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.federation.directives.FieldSet
import com.expedia.graphql.federation.directives.KeyDirective

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
