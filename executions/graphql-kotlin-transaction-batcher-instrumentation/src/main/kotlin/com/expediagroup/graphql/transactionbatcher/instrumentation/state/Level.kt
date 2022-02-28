package com.expediagroup.graphql.transactionbatcher.instrumentation.state

data class Level(val number: Int) {
    fun next(): Level = Level(number + 1)
    fun previous(): Level = Level(number - 1)
    fun isFirst(): Boolean = number == 1
}
