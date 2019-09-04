package com.expedia.graphql.sample.query

import org.springframework.stereotype.Component

@Component
class SimpleQuery : Query {
    fun dataFromExtendApp() = "hello from extend app"
}
