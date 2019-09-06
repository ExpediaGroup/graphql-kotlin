package com.expediagroup.graphql.generator.types

import com.expediagroup.graphql.TopLevelNames
import com.expediagroup.graphql.TopLevelObject
import com.expediagroup.graphql.exceptions.InvalidSubscriptionTypeException
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import graphql.schema.GraphQLFieldDefinition
import io.mockk.every
import io.reactivex.Flowable
import org.junit.jupiter.api.Test
import org.reactivestreams.Publisher
import kotlin.reflect.KFunction
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

internal class SubscriptionBuilderTest : TypeTestHelper() {

    @Test
    fun `given an empty list, it should not return a field`() {
        val builder = SubscriptionBuilder(generator)
        val result = builder.getSubscriptionObject(emptyList())
        assertNull(result)
    }

    @Test
    fun `give a valid list, it should properly set the top level name`() {
        val builder = SubscriptionBuilder(generator)
        val subscriptions = listOf(TopLevelObject(MyPublicTestSubscription()))
        every { config.topLevelNames } returns TopLevelNames(subscription = "FooBar")
        val result = builder.getSubscriptionObject(subscriptions)

        assertEquals(expected = "FooBar", actual = result?.name)
    }

    @Test
    fun `given a private class, it should throw an exception`() {
        val builder = SubscriptionBuilder(generator)
        val subscriptions = listOf(TopLevelObject(MyPrivateTestSubscription()))

        assertFailsWith(InvalidSubscriptionTypeException::class) {
            builder.getSubscriptionObject(subscriptions)
        }
    }

    @Test
    fun `given a function that returns a Publisher, it should add it to the schema`() {

        val builder = SubscriptionBuilder(generator)
        val subscriptions = listOf(TopLevelObject(MyPublicTestSubscription()))

        val result = builder.getSubscriptionObject(subscriptions)

        assertEquals(3, result?.fieldDefinitions?.size)
        assertNotNull(result?.fieldDefinitions?.find { it.name == "counter" })
        assertNotNull(result?.fieldDefinitions?.find { it.name == "flowabelCounter" })
        assertNotNull(result?.fieldDefinitions?.find { it.name == "filterMe" })
    }

    @Test
    fun `given custom hooks that filter functions, it should not generate those functions`() {
        val builder = SubscriptionBuilder(generator)
        val subscriptions = listOf(TopLevelObject(MyPublicTestSubscription()))

        class CustomHooks : SchemaGeneratorHooks {
            override fun isValidFunction(function: KFunction<*>) = function.name != "filterMe"
        }

        every { config.hooks } returns CustomHooks()

        val result = builder.getSubscriptionObject(subscriptions)

        assertEquals(2, result?.fieldDefinitions?.size)
        assertNotNull(result?.fieldDefinitions?.find { it.name == "counter" })
    }

    @Test
    fun `given custom hooks that change the field after generation, it should use the new fields`() {
        val builder = SubscriptionBuilder(generator)
        val subscriptions = listOf(TopLevelObject(MyPublicTestSubscription()))

        class CustomHooks : SchemaGeneratorHooks {
            override fun didGenerateSubscriptionType(function: KFunction<*>, fieldDefinition: GraphQLFieldDefinition): GraphQLFieldDefinition {
                return if (fieldDefinition.name == "filterMe") {
                    fieldDefinition.transform { fieldBuilder -> fieldBuilder.name("changedField") }
                } else fieldDefinition
            }
        }

        every { config.hooks } returns CustomHooks()

        val result = builder.getSubscriptionObject(subscriptions)

        assertEquals(3, result?.fieldDefinitions?.size)
        assertNotNull(result?.fieldDefinitions?.find { it.name == "changedField" })
    }
}

class MyPublicTestSubscription {
    fun counter(): Publisher<Int> = Flowable.just(1)

    fun flowabelCounter(): Flowable<Int> = Flowable.just(1)

    fun filterMe(): Publisher<Int> = Flowable.just(2)
}

private class MyPrivateTestSubscription {
    fun counter(): Publisher<Int> = Flowable.just(3)
}
