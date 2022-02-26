package com.expediagroup.graphql.server.spring.execution

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.util.AccessPattern

//TODO register with mapper
// class Defined<out U> @JsonCreator constructor(@JsonValue val value: U?) : OptionalInput<U>() {
/**
 * Null aware deserializer that distinguishes between undefined value, explicit NULL value
 * and specified value.
 */
class OptionalInputDeserializer(private val klazz: Class<*>? = null) : JsonDeserializer<OptionalInput<*>>(), ContextualDeserializer {

    override fun createContextual(ctxt: DeserializationContext, property: BeanProperty?): JsonDeserializer<*> {
        val type = if (property != null) {
            property.type.containedType(0)
        } else {
            ctxt.contextualType
        }
        return OptionalInputDeserializer(type.rawClass)
    }

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): OptionalInput<*> {
        val result: Any = ctxt.readValue(p, klazz)
        return OptionalInput.Defined(result)
    }

    override fun getNullAccessPattern(): AccessPattern = AccessPattern.CONSTANT

    override fun getNullValue(ctxt: DeserializationContext): OptionalInput<*> = if (ctxt.parser.parsingContext.currentName != null) {
        OptionalInput.Defined(null)
    } else {
        OptionalInput.Undefined
    }
}
