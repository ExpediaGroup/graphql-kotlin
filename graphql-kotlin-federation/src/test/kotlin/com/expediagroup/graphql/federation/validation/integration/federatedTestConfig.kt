package com.expediagroup.graphql.federation.validation.integration

import com.expediagroup.graphql.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorHooks

fun federatedTestConfig(supportedPackage: String) = FederatedSchemaGeneratorConfig(
    supportedPackages = listOf(supportedPackage),
    hooks = FederatedSchemaGeneratorHooks(emptyList())
)
