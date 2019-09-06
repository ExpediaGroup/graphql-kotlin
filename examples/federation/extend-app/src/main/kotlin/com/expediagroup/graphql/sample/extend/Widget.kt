package com.expediagroup.graphql.sample.extend

import com.expediagroup.graphql.federation.directives.ExtendsDirective
import com.expediagroup.graphql.federation.directives.ExternalDirective
import com.expediagroup.graphql.federation.directives.FieldSet
import com.expediagroup.graphql.federation.directives.KeyDirective
import com.expediagroup.graphql.federation.execution.FederatedTypeResolver
import kotlin.random.Random

@KeyDirective(fields = FieldSet("id"))
@ExtendsDirective
data class Widget(
    @property:ExternalDirective val id: Int,
    val randomValueFromExtend: Int
) {
    fun extraStringFromExtend() = "This data is coming from extend-app!"
}

class InvalidWidgetIdException : RuntimeException()

val widgetResolver = object : FederatedTypeResolver<Widget> {
    override suspend fun resolve(representations: List<Map<String, Any>>): List<Widget?> = representations.map {
        // Extract the 'id' from the other service
        val id = it["id"]?.toString()?.toIntOrNull() ?: throw InvalidWidgetIdException()

        // If we needed to construct a Widget which has data from other APIs,
        // this is the place where we could call them with the widget id
        val valueFromExtend = Random.nextInt()
        Widget(id, valueFromExtend)
    }
}
