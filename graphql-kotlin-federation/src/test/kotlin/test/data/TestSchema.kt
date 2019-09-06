package test.data

import com.expediagroup.graphql.federation.FederatedSchemaGeneratorConfig
import com.expediagroup.graphql.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.federation.execution.FederatedTypeRegistry
import com.expediagroup.graphql.federation.execution.FederatedTypeResolver
import com.expediagroup.graphql.federation.toFederatedSchema
import graphql.schema.GraphQLSchema

internal fun federatedTestSchema(federatedTypeResolvers: Map<String, FederatedTypeResolver<*>> = emptyMap()): GraphQLSchema {
    val config = FederatedSchemaGeneratorConfig(
        supportedPackages = listOf("test.data.queries.federated"),
        hooks = FederatedSchemaGeneratorHooks(FederatedTypeRegistry(federatedTypeResolvers))
    )

    return toFederatedSchema(config)
}
