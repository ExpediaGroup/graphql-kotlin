package com.expediagroup.graphql.exceptions

import kotlin.reflect.KParameter

/**
 * GraphQL Interfaces and Unions cannot be used as arguments.
 * Specification reference: https://facebook.github.io/graphql/draft/#sec-Field-Arguments
 *
 * This specification is translated as following:
 * the type of a function argument cannot be a java / kotlin interface.
 *
 * Example of invalid use cases:
 *
 * class InvalidQuery {
 *      fun invalidInterfaceUsage (arg: AnInterface): String = arg.value
 *
 *      fun invalidUnionUsage (arg: UnionMarkup): Int = arg.property
 * }
 *
 * interface AnInterface {
 *    val value: String
 * }
 *
 * data class AnImplementation (
 *    override val value: String = "something"
 * ) : AnInterface
 *
 *
 * interface UnionMarkup
 *
 * data class PartOfUnion( val property: Int) : UnionMarkup
 */
class InvalidInputFieldTypeException(kParameter: KParameter)
    : GraphQLKotlinException("Argument cannot be an interface or a union, $kParameter")
