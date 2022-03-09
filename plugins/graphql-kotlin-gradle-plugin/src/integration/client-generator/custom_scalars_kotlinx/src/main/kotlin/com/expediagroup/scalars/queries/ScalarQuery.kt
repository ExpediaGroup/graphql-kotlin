package com.expediagroup.scalars.queries

import com.expediagroup.graphql.generator.execution.OptionalInput
import com.expediagroup.graphql.server.operations.Query
import com.ibm.icu.util.ULocale
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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

    fun optionalScalarQuery(optional: OptionalInput<OptionalWrapperInput> = OptionalInput.Undefined): OptionalWrapper? {
        logger.info("optional query received: $optional")
        return when (optional) {
            is OptionalInput.Defined -> optional.value?.toOptionalWrapper()
            is OptionalInput.Undefined -> OptionalWrapper()
        }
    }

    fun scalarQuery(required: RequiredWrapper): RequiredWrapper {
        logger.info("required query received: $required")
        return required
    }
}
