package com.expedia.graphql.generator.extensions

import com.expedia.graphql.annotations.GraphQLContext
import com.expedia.graphql.annotations.GraphQLIgnore
import graphql.schema.DataFetchingEnvironment
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class KFunctionExtensionsKtTest {

    @Test
    fun getValidArguments() {
        val args = TestingClass::happyPath.getValidArguments()
        assertEquals(expected = 1, actual = args.size)
        assertEquals(expected = "color", actual = args.first().getName())
    }

    @Test
    fun `getValidArguments should ignore @GraphQLIgnore`() {
        val args = TestingClass::ignored.getValidArguments()
        assertEquals(expected = 1, actual = args.size)
        assertEquals(expected = "notIgnored", actual = args.first().getName())
    }

    @Test
    fun `getValidArguments should ignore @GraphQLContext`() {
        val args = TestingClass::context.getValidArguments()
        assertEquals(expected = 1, actual = args.size)
        assertEquals(expected = "notContext", actual = args.first().getName())
    }

    @Test
    fun `getValidArguments should ignore DataFetchingEnvironment`() {
        val args = TestingClass::dataFetchingEnvironment.getValidArguments()
        assertEquals(expected = 1, actual = args.size)
        assertEquals(expected = "notEnvironment", actual = args.first().getName())
    }

    private class TestingClass {
        fun happyPath(color: String) = "You're color is $color"

        fun ignored(@GraphQLIgnore ignoredArg: String, notIgnored: String) = "$ignoredArg and $notIgnored"

        fun context(@GraphQLContext contextArg: String, notContext: String) = "$contextArg and $notContext"

        fun dataFetchingEnvironment(environment: DataFetchingEnvironment, notEnvironment: String): String = "${environment.field.name} and $notEnvironment"
    }
}
