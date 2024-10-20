package com.expediagroup.graphql.server.spring

import com.alibaba.fastjson2.JSONWriter
import com.alibaba.fastjson2.support.config.FastJsonConfig
import com.alibaba.fastjson2.support.spring6.http.codec.Fastjson2Decoder
import com.alibaba.fastjson2.support.spring6.http.codec.Fastjson2Encoder
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
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
