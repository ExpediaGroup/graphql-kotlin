package com.expediagroup.graphql.apq.provider

class AutomaticPersistedQueriesExtension(map: Map<String, Any?>) {
    val version: Int by map
    val sha256Hash: String by map
}
