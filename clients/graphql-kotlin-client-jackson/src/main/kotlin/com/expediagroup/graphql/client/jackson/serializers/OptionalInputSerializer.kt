/*
 * Copyright 2021 Expedia, Inc
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

package com.expediagroup.graphql.client.jackson.serializers

import com.expediagroup.graphql.client.jackson.types.OptionalInput
import tools.jackson.core.JsonGenerator
import tools.jackson.databind.SerializationContext
import tools.jackson.databind.ValueSerializer

class OptionalInputSerializer : ValueSerializer<OptionalInput<*>>() {

    override fun isEmpty(ctxt: SerializationContext, value: OptionalInput<*>?): Boolean {
        return value == OptionalInput.Undefined
    }

    override fun serialize(value: OptionalInput<*>, gen: JsonGenerator, ctxt: SerializationContext) {
        when (value) {
            is OptionalInput.Undefined -> return
            is OptionalInput.Defined -> {
                if (value.value == null) {
                    ctxt.defaultNullValueSerializer.serialize(value.value, gen, ctxt)
                } else {
                    ctxt.findValueSerializer(value.value::class.java).serialize(value.value, gen, ctxt)
                }
            }
        }
    }
}
