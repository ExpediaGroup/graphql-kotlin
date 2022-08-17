package com.expediagroup.graphql.examples.client.server

import com.expediagroup.graphql.examples.client.server.scalars.graphqlULocaleType
import com.expediagroup.graphql.examples.client.server.scalars.graphqlUUIDType
import com.expediagroup.graphql.generator.federation.FederatedSchemaGeneratorHooks
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import com.ibm.icu.util.ULocale
import graphql.schema.GraphQLType
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.reflect.KType

@Component
class CustomFederatedHooks(resolvers: List<FederatedTypeResolver>) : FederatedSchemaGeneratorHooks(resolvers, true) {
    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
        UUID::class -> graphqlUUIDType
        ULocale::class -> graphqlULocaleType
        else -> super.willGenerateGraphQLType(type)
    }
}
