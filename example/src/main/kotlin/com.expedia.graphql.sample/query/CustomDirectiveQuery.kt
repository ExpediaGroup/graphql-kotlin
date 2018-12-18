package com.expedia.graphql.sample.query

import com.expedia.graphql.annotations.GraphQLDescription
import com.expedia.graphql.sample.directives.CakeOnly
import com.expedia.graphql.sample.directives.LowercaseDirective
import com.expedia.graphql.sample.directives.StringEval
import org.springframework.stereotype.Component

@Component
class CustomDirectiveQuery : Query {

    @GraphQLDescription("Returns a message modified by directives, lower case and non-empty")
    fun justWhisper(@StringEval(default = "default string", lowerCase = true) msg: String?): String? = msg

    @GraphQLDescription("This will only accept 'Cake' as input")
    @CakeOnly
    fun onlyCake(msg: String): String = "<3"

    @GraphQLDescription("Returns message modified by the manually wired directive to force lowercase")
    @LowercaseDirective
    fun forceLowercaseEcho(msg: String) = msg
}