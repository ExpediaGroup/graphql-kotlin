package test.data

import com.expedia.graphql.federation.FederatedSchemaGeneratorConfig
import com.expedia.graphql.federation.FederatedSchemaGeneratorHooks
import com.expedia.graphql.federation.execution.FederatedTypeRegistry
import com.expedia.graphql.federation.execution.FederatedTypeResolver
import com.expedia.graphql.federation.toFederatedSchema
import graphql.schema.GraphQLSchema

internal fun federatedTestSchema(federatedTypeResolvers: Map<String, FederatedTypeResolver<*>> = emptyMap()): GraphQLSchema {
    val config = FederatedSchemaGeneratorConfig(
        supportedPackages = listOf("test.data.queries.federated"),
        hooks = FederatedSchemaGeneratorHooks(FederatedTypeRegistry(federatedTypeResolvers))
    )

    return toFederatedSchema(config)
}
