package com.expediagroup.graphql.server.extensions

import com.alibaba.fastjson2.JSONReader

inline fun <reified T> JSONReader.readAsArray(): List<T> {
    val collector = mutableListOf<T>()
    readArray(collector, T::class.java)
    return collector
}

inline fun <reified T> JSONReader.readAs(): T = read(T::class.java)
