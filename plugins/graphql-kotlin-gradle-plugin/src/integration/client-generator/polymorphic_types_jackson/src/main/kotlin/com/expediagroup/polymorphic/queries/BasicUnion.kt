package com.expediagroup.polymorphic.queries

interface BasicUnion

data class Foo(val foo: String): BasicUnion
data class Bar(val bar: String): BasicUnion
data class Baz(val baz: String?): BasicUnion
