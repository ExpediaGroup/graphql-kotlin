package com.expediagroup.graphql.generator.federation.data.integration.intfObject

import com.expediagroup.graphql.generator.federation.data.queries.federated.v1.Review
import com.expediagroup.graphql.generator.federation.directives.FieldSet
import com.expediagroup.graphql.generator.federation.directives.InterfaceObjectDirective
import com.expediagroup.graphql.generator.federation.directives.KeyDirective
import com.expediagroup.graphql.generator.scalars.ID

class IntfObjectQuery {

    fun product(id: ID): Product = TODO()
}

@InterfaceObjectDirective
@KeyDirective(fields = FieldSet("id"))
data class Product(val id: ID) {
    fun reviews(): List<Review> = TODO()
}
