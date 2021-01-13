/*
 * Copyright 2020 Expedia, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.expediagroup.graphql.generator.execution

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.fasterxml.jackson.databind.util.AccessPattern

/**
 * Wrapper used to represent optionally defined input arguments that allows us to distinguish between undefined value, explicit NULL value
 * and specified value.
 */
@JsonDeserialize(using = OptionalInputDeserializer::class)
sealed class OptionalInput<out T> {

    /**
     * Represents missing/undefined value.
     */
    object Undefined : OptionalInput<Nothing>() {
        override fun toString() = "UNDEFINED"
    }

    /**
     * Wrapper holding explicitly specified value including NULL.
     */
    class Defined<out U> @JsonCreator constructor(@JsonValue val value: U?) : OptionalInput<U>() {
        override fun toString(): String = "Defined(value=$value)"
    }
}

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
