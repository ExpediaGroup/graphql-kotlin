package com.expediagroup.scalars.queries

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.server.operations.Query
import com.ibm.icu.util.ULocale
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ScalarQuery : Query {

    private val logger: Logger = LoggerFactory.getLogger(ScalarQuery::class.java)

    fun localeQuery(optional: OptionalInput<ULocale>): ULocale? = when (optional) {
        is OptionalInput.Undefined -> {
            logger.info("undefined locale, defaulting to en_US")
            ULocale.US
        }
        is OptionalInput.Defined<ULocale> -> {
            logger.info("locale is defined, locale = ${optional.value}")
            optional.value
        }
    }

    fun optionalScalarQuery(optional: OptionalWrapper? = null): OptionalWrapper? {
        logger.info("optional query received: $optional")
        return optional
    }

    fun scalarQuery(required: RequiredWrapper): RequiredWrapper {
        logger.info("required query received: $required")
        return required
    }
}
