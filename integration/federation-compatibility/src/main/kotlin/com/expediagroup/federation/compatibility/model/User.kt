package com.expediagroup.federation.compatibility.model

import com.expediagroup.graphql.generator.federation.directives.*
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeResolver
import com.expediagroup.graphql.generator.federation.execution.FederatedTypeSuspendResolver
import com.expediagroup.graphql.generator.scalars.ID
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Component
import kotlin.math.roundToInt
import kotlin.properties.Delegates

val DEFAULT_USER = User(email = ID("support@apollographql.com"), name = "Jane Smith", totalProductsCreated = 1337)

/*
extend type User @key(fields: "email") {
  averageProductsCreatedPerYear: Int @requires(fields: "totalProductsCreated yearsOfEmployment")
  email: ID! @external
  name: String @override(from: "users")
  totalProductsCreated: Int @external
  yearsOfEmployment: Int! @external
}
 */
@KeyDirective(fields = FieldSet("email"))
@ExtendsDirective
data class User(
    @ExternalDirective
    val email: ID,
    @OverrideDirective(from = "users")
    val name: String?,
    @ExternalDirective
    var totalProductsCreated: Int? = null
) {
    @ExternalDirective
    var yearsOfEmployment: Int by Delegates.notNull()

    @RequiresDirective(fields = FieldSet("totalProductsCreated yearsOfEmployment"))
    fun averageProductsCreatedPerYear(): Int? = totalProductsCreated?.let { totalCount ->
        (1.0f * totalCount / yearsOfEmployment).roundToInt()
    }
}

@Component
class UserResolver : FederatedTypeSuspendResolver<User> {
    override val typeName: String = "User"

    override suspend fun resolve(
        environment: DataFetchingEnvironment,
        representation: Map<String, Any>
    ): User? {
        val email = representation["email"]?.toString() ?: throw RuntimeException("invalid entity reference")
        val user = User(email = ID(email), name = "Jane Smith", totalProductsCreated = 1337)
        representation["totalProductsCreated"]?.toString()?.toIntOrNull()?.let { totalProductsCreated ->
            user.totalProductsCreated = totalProductsCreated
        }
        representation["yearsOfEmployment"]?.toString()?.toIntOrNull()?.let { yearsOfEmployment ->
            user.yearsOfEmployment = yearsOfEmployment
        }
        return user
    }
}
