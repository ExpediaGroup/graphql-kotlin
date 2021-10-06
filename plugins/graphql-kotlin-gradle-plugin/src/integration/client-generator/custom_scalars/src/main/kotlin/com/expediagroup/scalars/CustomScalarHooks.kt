package com.expediagroup.scalars

import com.expediagroup.graphql.generator.hooks.SchemaGeneratorHooks
import com.expediagroup.graphql.plugin.schema.hooks.SchemaGeneratorHooksProvider
import com.expediagroup.scalars.types.graphqlLocaleType
import com.expediagroup.scalars.types.graphqlUUIDType
import com.ibm.icu.util.ULocale
import graphql.schema.GraphQLType
import java.util.UUID
import kotlin.reflect.KType

object CustomScalarHooks : SchemaGeneratorHooks {
    override fun willGenerateGraphQLType(type: KType): GraphQLType? = when (type.classifier) {
        ULocale::class -> graphqlLocaleType
        UUID::class -> graphqlUUIDType
        else -> null
    }
}

class CustomScalarHooksProvider : SchemaGeneratorHooksProvider {
    override fun hooks(): SchemaGeneratorHooks = CustomScalarHooks
}