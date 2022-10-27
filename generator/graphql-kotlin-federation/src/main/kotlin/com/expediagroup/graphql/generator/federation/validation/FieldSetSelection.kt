package com.expediagroup.graphql.generator.federation.validation

/**
 * Simple representation of a FieldSet selection set.
 */
internal data class FieldSetSelection(val field: String, val subSelections: MutableList<FieldSetSelection> = ArrayList())
