package com.expedia.graphql.sample.model

import com.expedia.graphql.annotations.GraphQLDefaultValue
import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.annotations.GraphQLIgnore

@GraphQLDescription("A useful widget")
data class Widget(
    @property:GraphQLDescription("The widget's value that can be null")
    var value: Int? = null,
    @property:Deprecated(message = "This field is deprecated", replaceWith = ReplaceWith("value"))
    @property:GraphQLDescription("The widget's deprecated value that shouldn't be used")
    val deprecatedValue: Int? = value,
    @property:GraphQLIgnore
    val ignoredField: String? = "ignored",
    private val hiddenField: String? = "hidden",
    @property:GraphQLDefaultValue("UNICORN")
    var type: String? = "UNICORN"
) {

    @GraphQLDescription("returns original value multiplied by target OR null if original value was null")
    fun multiplyValueBy(multiplier: Int) = value?.times(multiplier)
}