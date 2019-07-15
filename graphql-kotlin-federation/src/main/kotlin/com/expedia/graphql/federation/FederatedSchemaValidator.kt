package com.expedia.graphql.federation

import com.expedia.graphql.federation.directives.FieldSet
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeUtil

/**
 * Validates generated federated objects.
 */
class FederatedSchemaValidator {

    val errors = mutableListOf<String>()

    /**
     * Validates target GraphQLType whether it is a valid federated object.
     *
     * Verifies:
     * - base type doesn't declare any @external fields
     * - @key directive references existing fields
     * - @key directive on extended types references @external fields
     * - @requires directive is only applicable on extended types and references @external fields
     * - @provides directive references valid @external fields
     */
    fun validateGraphQLType(type: GraphQLType) {
        val unwrappedType = GraphQLTypeUtil.unwrapAll(type)
        if (unwrappedType is GraphQLObjectType && unwrappedType.isFederatedType()) {
            validate(unwrappedType.name, unwrappedType.fieldDefinitions, unwrappedType.directivesByName)
        } else if (unwrappedType is GraphQLInterfaceType && unwrappedType.isFederatedType()) {
            validate(unwrappedType.name, unwrappedType.fieldDefinitions, unwrappedType.directivesByName)
        }
    }

    @Suppress("ComplexMethod")
    private fun validate(federatedType: String, fields: List<GraphQLFieldDefinition>, directives: Map<String, GraphQLDirective>) {
        val fieldMap = fields.associateBy { it.name }
        val extendedType = directives.containsKey("extends")

        // [OK]    @key on @extended type references @external fields
        // [ERROR] @key references fields resulting in list
        // [ERROR] @key references fields resulting in union
        // [ERROR] @key references fields resulting in interface
        validateDirective(federatedType, "key", directives, fieldMap, extendedType)

        val requiresFields = fields.filter { it.getDirective("requires") != null }
        if (extendedType) {
            // validate @requires field set
            // [OK]    @requires references valid field set marked @external
            // [ERROR] @requires specified on base type
            // [ERROR] @requires specifies non-existent fields
            for (fieldWithRequiresDirective in requiresFields) {
                validateDirective("$federatedType.${fieldWithRequiresDirective.name}", "requires", fieldWithRequiresDirective.directivesByName, fieldMap, extendedType)
            }
        } else {
            if (requiresFields.isNotEmpty()) {
                errors.add("base $federatedType type has fields marked with @requires directive, fields=${requiresFields.map { it.name }}")
            }

            val externalFields = fields.filter { it.getDirective("external") != null }.map { it.name }
            if (externalFields.isNotEmpty()) {
                errors.add("base $federatedType type has fields marked with @external directive, fields=$externalFields")
            }
        }

        // [OK]    @provides on base type references valid @external fields on @extend object
        // [ERROR] @provides on base type references local object fields
        // [ERROR] @provides on base type references local fields on @extends object
        // [ERROR] @provides references interface type
        // [OK]    @provides references list of valid @extend objects
        // [ERROR] @provides references @external list field
        // [ERROR] @provides references @external interface field
        val providesFields = fields.filter { it.getDirective("provides") != null }
        for (fieldWithProvidesDirective in providesFields) {
            val returnType = GraphQLTypeUtil.unwrapAll(fieldWithProvidesDirective.type)
            if (returnType is GraphQLObjectType) {
                if (!returnType.isExtendedType()) {
                    errors.add("@provides directive is specified on a $federatedType ${fieldWithProvidesDirective.name} field references local object")
                } else {
                    val returnTypeFields = returnType.fieldDefinitions.associateBy { it.name }
                    // @provides is applicable on both base and federated types and always references @external fields
                    validateDirective("$federatedType.${fieldWithProvidesDirective.name}", "provides", fieldWithProvidesDirective.directivesByName, returnTypeFields, true)
                }
            } else {
                errors.add("@provides directive is specified on a $federatedType ${fieldWithProvidesDirective.name} field but it does not return an object type")
            }
        }
    }

    private fun validateDirective(validatedType: String, targetDirective: String, directives: Map<String, GraphQLDirective>, fieldMap: Map<String, GraphQLFieldDefinition>, extendedType: Boolean) {
        val directive = directives[targetDirective]
        if (directive == null) {
            errors.add("@$targetDirective directive is missing on federated $validatedType type")
        } else {
            val fieldSetValue = (directive.getArgument("fields")?.value as? FieldSet)?.value
            val fieldSet = fieldSetValue?.split(" ")?.filter { it.isNotEmpty() }.orEmpty()
            if (fieldSet.isEmpty()) {
                errors.add("@$targetDirective directive on $validatedType is missing field information")
            } else {
                // validate key field set
                val validatedDirectiveInfo = "@$targetDirective(fields = $fieldSetValue) directive on $validatedType"
                validateFieldSelection(validatedDirectiveInfo, fieldSet.iterator(), fieldMap, extendedType)
            }
        }
    }

    @Suppress("ComplexMethod", "NestedBlockDepth")
    private fun validateFieldSelection(validatedDirective: String, iterator: Iterator<String>, fields: Map<String, GraphQLFieldDefinition>, extendedType: Boolean = false) {
        var previousField: String? = null
        while (iterator.hasNext()) {
            val currentField = iterator.next()
            when (currentField) {
                "{" -> {
                    val targetField = fields[previousField]?.type
                    when (val unwrappedType = GraphQLTypeUtil.unwrapAll(targetField)) {
                        is GraphQLInterfaceType -> validateFieldSelection(validatedDirective, iterator, unwrappedType.fieldDefinitions.associateBy { it.name }, extendedType)
                        is GraphQLObjectType -> validateFieldSelection(validatedDirective, iterator, unwrappedType.fieldDefinitions.associateBy { it.name }, extendedType)
                        else -> errors.add("$validatedDirective specifies invalid field set - field set defines nested selection set on unsupported type")
                    }
                }
                "}" -> return
                else -> {
                    val targetField = fields[currentField]
                    if (null != targetField) {
                        // only valid if BOTH conditions are true/false, e.g. if we are validating extended type its fields have to be marked external and base type cannot have external fields
                        val externalField = targetField.getDirective("external") != null
                        if (extendedType xor externalField) {
                            errors.add("$validatedDirective specifies invalid field set - type incorrectly references external/base fields")
                        }
                    } else {
                        errors.add("$validatedDirective specifies invalid field set - field set specifies fields that do not exist")
                    }
                }
            }

            previousField = currentField
        }
    }

    private fun GraphQLDirectiveContainer.isFederatedType() = this.getDirective("key") != null || isExtendedType()

    private fun GraphQLDirectiveContainer.isExtendedType() = this.getDirective("extends") != null
}
