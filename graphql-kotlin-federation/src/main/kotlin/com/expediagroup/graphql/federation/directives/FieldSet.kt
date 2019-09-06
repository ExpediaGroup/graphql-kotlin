package com.expediagroup.graphql.federation.directives

/**
 * Annotation representing _FieldSet scalar type that is used to represent a set of fields.
 *
 * Field set can represent:
 * - single field, e.g. "id"
 * - multiple fields, e.g. "id name"
 * - nested selection sets, e.g. "id user { name }"
 *
 * @param value field set that represents a set of fields forming the key
 *
 * @see com.expediagroup.graphql.federation.types.FIELD_SET_SCALAR_TYPE
 */
annotation class FieldSet(val value: String)
