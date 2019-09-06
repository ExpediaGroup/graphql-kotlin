package com.expediagroup.graphql.sample.query

import org.springframework.stereotype.Component
import javax.validation.Valid
import javax.validation.constraints.Pattern

@Component
class ValidatedQuery : Query {
    fun argumentWithValidation(@Valid arg: TypeWithPattern): String = arg.lowerCaseOnly
}

data class TypeWithPattern(
        @field:Pattern(regexp = "[a-z]*")
        val lowerCaseOnly: String
)
