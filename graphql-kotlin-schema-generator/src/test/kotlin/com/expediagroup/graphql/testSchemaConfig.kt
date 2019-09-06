package com.expediagroup.graphql

import com.expediagroup.graphql.directives.KotlinDirectiveWiringFactory
import com.expediagroup.graphql.directives.KotlinSchemaDirectiveWiring
import com.expediagroup.graphql.hooks.SchemaGeneratorHooks
import io.mockk.every
import io.mockk.spyk

val defaultSupportedPackages = listOf("com.expediagroup")
val testSchemaConfig = SchemaGeneratorConfig(defaultSupportedPackages)

fun getTestSchemaConfigWithHooks(hooks: SchemaGeneratorHooks) = SchemaGeneratorConfig(defaultSupportedPackages, hooks = hooks)

fun getTestSchemaConfigWithMockedDirectives() = getTestSchemaConfigWithHooks(hooks = object : SchemaGeneratorHooks {
    override val wiringFactory: KotlinDirectiveWiringFactory
        get() = spyk(KotlinDirectiveWiringFactory()) {
            every { getSchemaDirectiveWiring(any()) } returns object : KotlinSchemaDirectiveWiring {}
        }
})
