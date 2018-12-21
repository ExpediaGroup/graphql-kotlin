package com.expedia.graphql.generator.extensions

import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class BooleanExtensionsKtTest {

    @Test
    fun isTrue() {
        assertFalse(null.isTrue())
        assertFalse(false.isTrue())
        assertTrue(true.isTrue())
    }
}
