package com.expedia.graphql

import com.expedia.graphql.directives.KotlinDirectiveWiringFactory
import com.expedia.graphql.directives.KotlinSchemaDirectiveWiring
import com.expedia.graphql.hooks.SchemaGeneratorHooks
import io.mockk.every
import io.mockk.spyk

val defaultSupportedPackages = listOf("com.expedia")
val testSchemaConfig = SchemaGeneratorConfig(defaultSupportedPackages)

fun getTestSchemaConfigWithHooks(hooks: SchemaGeneratorHooks) = SchemaGeneratorConfig(defaultSupportedPackages, hooks = hooks)

fun getTestSchemaConfigWithMockedDirectives() = getTestSchemaConfigWithHooks(hooks = object : SchemaGeneratorHooks {
    override val wiringFactory: KotlinDirectiveWiringFactory
        get() = spyk(KotlinDirectiveWiringFactory()) {
            every { getSchemaDirectiveWiring(any()) } returns object : KotlinSchemaDirectiveWiring {}
        }
})
