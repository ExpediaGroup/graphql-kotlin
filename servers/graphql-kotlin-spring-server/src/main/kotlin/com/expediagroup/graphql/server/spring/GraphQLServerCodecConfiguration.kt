/*
 * Copyright 2024 Expedia, Inc
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
package com.expediagroup.graphql.server.spring

import com.alibaba.fastjson2.JSONWriter
import com.alibaba.fastjson2.support.config.FastJsonConfig
import com.alibaba.fastjson2.support.spring6.http.codec.Fastjson2Decoder
import com.alibaba.fastjson2.support.spring6.http.codec.Fastjson2Encoder
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@EnableWebFlux
class GraphQLServerCodecConfiguration(
    private val config: GraphQLConfigurationProperties,
    private val objectMapper: ObjectMapper,
) : WebFluxConfigurer {
    override fun configureHttpMessageCodecs(configurer: ServerCodecConfigurer) {
        if (config.serializationLibrary == SerializationLibrary.FASTJSON) {
            configurer.defaultCodecs().apply {
                jackson2JsonDecoder(Fastjson2Decoder(objectMapper))
                jackson2JsonEncoder(
                    Fastjson2Encoder(
                        objectMapper,
                        FastJsonConfig().also {
                            it.setWriterFeatures(
                                JSONWriter.Feature.LargeObject,
                                JSONWriter.Feature.WriteNulls,
                            )
                        },
                    )
                )
            }
        }
    }
}
