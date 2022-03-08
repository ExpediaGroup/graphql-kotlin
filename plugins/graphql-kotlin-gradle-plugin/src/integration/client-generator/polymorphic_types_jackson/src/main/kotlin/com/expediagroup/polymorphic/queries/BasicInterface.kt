package com.expediagroup.polymorphic.queries

interface BasicInterface {
    val id: String
}

data class FooImplementation(override val id: String, val foo: String) : BasicInterface
data class BarImplementation(override val id: String, val bar: String?) : BasicInterface
