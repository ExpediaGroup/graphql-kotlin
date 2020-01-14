package com.expediagroup.graphql.federation.validation.integration

import com.expediagroup.graphql.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.federation.execution.FederatedTypeRegistry

fun federatedTestConfig(supportedPackage: String) = FederatedSchemaGeneratorConfig(
    supportedPackages = listOf(supportedPackage),
    hooks = FederatedSchemaGeneratorHooks(FederatedTypeRegistry())
)
