package com.expedia.graphql.generator.extensions

import com.expedia.graphql.exceptions.CouldNotCastToKClassException
import com.expedia.graphql.exceptions.CouldNotGetNameOfKTypeException
import com.expedia.graphql.exceptions.InvalidListTypeException
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

@Throws(InvalidListTypeException::class)
internal fun KType.getTypeOfFirstArgument(): KType =
    this.arguments.firstOrNull()?.type ?: throw InvalidListTypeException(this)

@Throws(CouldNotCastToKClassException::class)
internal fun KType.getKClass() = this.classifier as? KClass<*> ?: throw CouldNotCastToKClassException(this)

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

@Throws(CouldNotGetNameOfKTypeException::class)
internal fun KType.getSimpleName(): String =
    this.jvmErasure.simpleName ?: throw CouldNotGetNameOfKTypeException(this)

internal val KType.qualifiedName: String
    get() = this.jvmErasure.qualifiedName ?: ""
