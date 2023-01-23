package com.expediagroup.graphql

import com.expediagroup.android.generated.ExampleQuery
import com.expediagroup.android.generated.inputs.SimpleArgumentInput
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class AndroidTests {

    @Test
    fun `verifies graphql client was generated`() {
        assertDoesNotThrow {
            ExampleQuery(variables = ExampleQuery.Variables(simpleCriteria = SimpleArgumentInput()))
        }
    }
}
