package com.expediagroup.graphql.examples.server.spring.query

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.server.operations.Query
import org.springframework.stereotype.Component

@Component
class OptionalInputQuery : Query {
    fun optionalListInput(patient: OptionalInput<List<PhoneNumber>>): String {
        return patient
            .let { if (it is OptionalInput.Defined) it.value else null }
            ?.map { it.number }
            .toString()
    }

    data class PhoneNumber(val number: Long)
}
