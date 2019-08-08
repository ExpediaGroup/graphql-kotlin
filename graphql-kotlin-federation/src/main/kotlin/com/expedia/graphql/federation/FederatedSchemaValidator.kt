package com.expedia.graphql.federation

import com.expedia.graphql.federation.directives.FieldSet
import graphql.schema.GraphQLDirective
import graphql.schema.GraphQLDirectiveContainer
import graphql.schema.GraphQLFieldDefinition
import graphql.schema.GraphQLInterfaceType
import graphql.schema.GraphQLList
import graphql.schema.GraphQLObjectType
import graphql.schema.GraphQLType
import graphql.schema.GraphQLTypeUtil
import graphql.schema.GraphQLUnionType

/**
 * Validates generated federated objects.
 */
class FederatedSchemaValidator {

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

    private fun validate(federatedType: String, fields: List<GraphQLFieldDefinition>, directives: Map<String, GraphQLDirective>) {
        val errors = mutableListOf<String>()
        val fieldMap = fields.associateBy { it.name }
        val extendedType = directives.containsKey("extends")

        // [OK]    @key directive is specified
        // [OK]    @key references valid existing fields
        // [OK]    @key on @extended type references @external fields
        // [ERROR] @key references fields resulting in list
        // [ERROR] @key references fields resulting in union
        // [ERROR] @key references fields resulting in interface
        errors.addAll(validateDirective(federatedType, "key", directives, fieldMap, extendedType))

        for (field in fields) {
            if (field.getDirective("requires") != null) {
                errors.addAll(validateRequiresDirective(federatedType, field, fieldMap, extendedType))
            }

            if (field.getDirective("provides") != null) {
                errors.addAll(validateProvidesDirective(federatedType, field))
            }
        }

        // [ERROR] federated base type references @external fields
        if (!extendedType) {
            val externalFields = fields.filter { it.getDirective("external") != null }.map { it.name }
            if (externalFields.isNotEmpty()) {
                errors.add("base $federatedType type has fields marked with @external directive, fields=$externalFields")
            }
        }

        if (errors.isNotEmpty()) {
            throw InvalidFederatedSchema(errors)
        }
    }

    // [OK]    @requires references valid fields marked @external
    // [ERROR] @requires specified on base type
    // [ERROR] @requires specifies non-existent fields
    private fun validateRequiresDirective(validatedType: String, validatedField: GraphQLFieldDefinition, fieldMap: Map<String, GraphQLFieldDefinition>, extendedType: Boolean): List<String> {
        val errors = mutableListOf<String>()
        if (extendedType) {
            errors.addAll(validateDirective("$validatedType.${validatedField.name}", "requires", validatedField.directivesByName, fieldMap, extendedType))
        } else {
            errors.add("base $validatedType type has fields marked with @requires directive, validatedField=${validatedField.name}")
        }
        return errors
    }

    // [OK]    @provides on base type references valid @external fields on @extend object
    // [ERROR] @provides on base type references local object fields
    // [ERROR] @provides on base type references local fields on @extends object
    // [ERROR] @provides references interface type
    // [OK]    @provides references list of valid @extend objects
    // [ERROR] @provides references @external list field
    // [ERROR] @provides references @external interface field
    private fun validateProvidesDirective(federatedType: String, field: GraphQLFieldDefinition): List<String> {
        val errors = mutableListOf<String>()
        val returnType = GraphQLTypeUtil.unwrapAll(field.type)
        if (returnType is GraphQLObjectType) {
            if (!returnType.isExtendedType()) {
                errors.add("@provides directive is specified on a $federatedType.${field.name} field references local object")
            } else {
                val returnTypeFields = returnType.fieldDefinitions.associateBy { it.name }
                // @provides is applicable on both base and federated types and always references @external fields
                errors.addAll(
                    validateDirective(
                        "$federatedType.${field.name}",
                        "provides",
                        field.directivesByName,
                        returnTypeFields,
                        true))
            }
        } else {
            errors.add("@provides directive is specified on a $federatedType.${field.name} field but it does not return an object type")
        }
        return errors
    }

    private fun validateDirective(
        validatedType: String,
        targetDirective: String,
        directives: Map<String, GraphQLDirective>,
        fieldMap: Map<String, GraphQLFieldDefinition>,
        extendedType: Boolean
    ): List<String> {
        val validationErrors = mutableListOf<String>()
        val directive = directives[targetDirective]

        if (directive == null) {
            validationErrors.add("@$targetDirective directive is missing on federated $validatedType type")
        } else {
            val fieldSetValue = (directive.getArgument("fields")?.value as? FieldSet)?.value
            val fieldSet = fieldSetValue?.split(" ")?.filter { it.isNotEmpty() }.orEmpty()
            if (fieldSet.isEmpty()) {
                validationErrors.add("@$targetDirective directive on $validatedType is missing field information")
            } else {
                // validate key field set
                val validatedDirectiveInfo = "@$targetDirective(fields = $fieldSetValue) directive on $validatedType"
                validateFieldSelection(validatedDirectiveInfo, fieldSet.iterator(), fieldMap, extendedType, validationErrors)
            }
        }
        return validationErrors
    }

    private fun validateFieldSelection(
        validatedDirective: String,
        iterator: Iterator<String>,
        fields: Map<String, GraphQLFieldDefinition>,
        extendedType: Boolean = false,
        errors: MutableList<String>
    ) {
        var previousField: String? = null
        while (iterator.hasNext()) {
            val currentField = iterator.next()
            when (currentField) {
                "{" -> {
                    val targetField = fields[previousField]?.type
                    when (val unwrappedType = GraphQLTypeUtil.unwrapAll(targetField)) {
                        is GraphQLInterfaceType -> validateFieldSelection(validatedDirective, iterator, unwrappedType.fieldDefinitions.associateBy { it.name }, extendedType, errors)
                        is GraphQLObjectType -> validateFieldSelection(validatedDirective, iterator, unwrappedType.fieldDefinitions.associateBy { it.name }, extendedType, errors)
                        else -> errors.add("$validatedDirective specifies invalid field set - field set defines nested selection set on unsupported type")
                    }
                }
                "}" -> return
                else -> validateKeySetField(fields[currentField], extendedType, errors, validatedDirective)
            }
            previousField = currentField
        }
    }

    private fun validateKeySetField(targetField: GraphQLFieldDefinition?, extendedType: Boolean, errors: MutableList<String>, validatedDirective: String) {
        if (null != targetField) {
            val externalField = targetField.getDirective("external") != null
            if (extendedType && !externalField) {
                errors.add("$validatedDirective specifies invalid field set - extended type incorrectly references local field=${targetField.name}")
            } else if (!extendedType && externalField) {
                errors.add("$validatedDirective specifies invalid field set - type incorrectly references external field=${targetField.name}")
            }

            when (GraphQLTypeUtil.unwrapNonNull(targetField.type)) {
                is GraphQLList -> errors.add("$validatedDirective specifies invalid field set - field set references GraphQLList, field=${targetField.name}")
                is GraphQLInterfaceType -> errors.add("$validatedDirective specifies invalid field set - field set references GraphQLInterfaceType, field=${targetField.name}")
                is GraphQLUnionType -> errors.add("$validatedDirective specifies invalid field set - field set references GraphQLUnionType, field=${targetField.name}")
            }
        } else {
            errors.add("$validatedDirective specifies invalid field set - field set specifies fields that do not exist")
        }
    }

    private fun GraphQLDirectiveContainer.isFederatedType() = this.getDirective("key") != null || isExtendedType()

    private fun GraphQLDirectiveContainer.isExtendedType() = this.getDirective("extends") != null
}
