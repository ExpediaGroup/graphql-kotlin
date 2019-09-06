package com.expediagroup.graphql.sample.query

import org.springframework.stereotype.Component

@Component
class SimpleQuery : Query {
    fun dataFromBaseApp() = "hello from base app"
}
