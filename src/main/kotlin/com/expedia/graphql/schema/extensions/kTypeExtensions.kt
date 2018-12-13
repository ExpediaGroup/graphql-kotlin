package com.expedia.graphql.schema.extensions

import com.expedia.graphql.schema.exceptions.InvalidListTypeException
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf

@Throws(InvalidListTypeException::class)
internal fun KType.getTypeOfFirstArgument(): KType =
    this.arguments.firstOrNull()?.type ?: throw InvalidListTypeException(this)

@Suppress("Detekt.UnsafeCast")
internal fun KType.getKClass() = this.classifier as KClass<*>

internal fun KType.getArrayType(): KType {
    val kClass = this.getKClass()
    return when {
        kClass.isSubclassOf(IntArray::class) -> Int::class.createType()
        kClass.isSubclassOf(LongArray::class) -> Long::class.createType()
        kClass.isSubclassOf(ShortArray::class) -> Short::class.createType()
        kClass.isSubclassOf(FloatArray::class) -> Float::class.createType()
        kClass.isSubclassOf(DoubleArray::class) -> Double::class.createType()
        kClass.isSubclassOf(CharArray::class) -> Char::class.createType()
        kClass.isSubclassOf(BooleanArray::class) -> Boolean::class.createType()
        else -> this.getTypeOfFirstArgument()
    }
}
