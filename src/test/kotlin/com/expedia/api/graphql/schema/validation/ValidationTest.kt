package com.expedia.api.graphql.schema.validation

import com.expedia.graphql.TopLevelObjectDef
import com.expedia.graphql.schema.exceptions.ValidationException
import com.expedia.graphql.schema.testSchemaConfig
import com.expedia.graphql.toSchema
import graphql.ExceptionWhileDataFetching
import graphql.GraphQL
import graphql.schema.GraphQLInputObjectType
import org.junit.Test
import javax.validation.Valid
import javax.validation.constraints.Min
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidationTest {

    @Test
    fun `A valid query is executed as expected`() {
        val schema = toSchema(
                queries = listOf(TopLevelObjectDef(MutationWithValidation())),
                config = testSchemaConfig
        )
        val graphQL = GraphQL.newGraphQL(schema).build()

        val result = graphQL.execute("query { triggerValidation(argumentWithValidation: { valueWithConstraint:6 }) }")
        assertEquals(0, result.errors.size)

        val data: Map<String, String>? = result.getData()
        assertEquals("It passed the validation", data!!["triggerValidation"])
    }

    @Test
    fun `A invalid mutation returns the failed constraints`() {
        val schema = toSchema(
                queries = emptyList(),
                mutations = listOf(TopLevelObjectDef(MutationWithValidation())),
                config = testSchemaConfig
        )
        val graphQL = GraphQL.newGraphQL(schema).build()

        val result = graphQL.execute("mutation { triggerValidation(argumentWithValidation: { valueWithConstraint:0 }) }")
        val errors = result.errors
        assertEquals(1, errors.size)
        val dataFetchingException = errors[0] as ExceptionWhileDataFetching

        val validationException = dataFetchingException.exception as ValidationException
        assertEquals(1, validationException.errors.size)
        val validationError = validationException.errors[0]
        assertEquals("valueWithConstraint", validationError.path)
        assertEquals("must be greater than or equal to 5", validationError.message)
        assertEquals("TypeWithConstraint(valueWithConstraint=0)", validationError.type)
    }

    @Test
    fun `Top level mutation argument must be annotated with @Valid to trigger the validation`() {
        val schema = toSchema(
                queries = emptyList(),
                mutations = listOf(TopLevelObjectDef(MutationWithValidation())),
                config = testSchemaConfig
        )
        val graphQL = GraphQL.newGraphQL(schema).build()

        val result = graphQL.execute("mutation { willNotTriggerValidation(argumentWithoutValidation: { valueWithConstraint:0}) }")
        assertEquals(0, result.errors.size)

        val data: Map<String, String>? = result.getData()
        assertEquals("The argument must be @Valid to trigger the validation", data!!["willNotTriggerValidation"])
    }

    @Test
    fun `Query arguments need validation are listed in the description`() {
        val schema = toSchema(
                queries = listOf(TopLevelObjectDef(MutationWithValidation())),
                config = testSchemaConfig
        )

        val fieldDefinition = schema.queryType.getFieldDefinition("triggerValidation")
        assertTrue(fieldDefinition.description.contains("Arguments with validation"))
        assertTrue(fieldDefinition.description.contains("argumentWithValidation"))
    }

    @Test
    fun `Validation constraints are added to the type description`() {
        val schema = toSchema(
                queries = listOf(TopLevelObjectDef(MutationWithValidation())),
                config = testSchemaConfig
        )

        val type = schema.typeMap["TypeWithConstraintInput"] as GraphQLInputObjectType
        assertTrue(type.description.contains("Properties with validation"))
        assertTrue(type.description.contains("valueWithConstraint"))
        assertTrue(type.description.contains("Min"))
        assertTrue(type.description.contains("value=5"))
    }
}

class MutationWithValidation {
    fun triggerValidation(@Valid argumentWithValidation: TypeWithConstraint): String = "It passed the validation"
    fun willNotTriggerValidation(argumentWithoutValidation: TypeWithConstraint): String = "The argument must be @Valid to trigger the validation"
}

data class TypeWithConstraint(
   @get:Min(5) val valueWithConstraint: Int
)