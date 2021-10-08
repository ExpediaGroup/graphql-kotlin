package com.expediagroup.scalars

import com.expediagroup.graphql.generator.scalars.ID
import com.expediagroup.graphql.server.operations.Query
import com.ibm.icu.util.ULocale
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.UUID
import kotlin.random.Random

@Component
class ScalarQuery : Query {

    private val logger: Logger = LoggerFactory.getLogger(ScalarQuery::class.java)
    private val random: Random = Random(1337)

    fun scalarQuery(required: ULocale, optional: ULocale? = null, wrapper: ScalarWrapper? = null): ScalarWrapper {
        logger.info("received: required $required, optional $optional, wrapper $wrapper")
        return wrapper ?: ScalarWrapper(
            count = random.nextInt(),
            id = ID(random.nextInt().toString()),
            name = "test",
            locale = ULocale.US,
            localeList = listOf(ULocale.UK, ULocale.FRANCE),
            rating = random.nextDouble(),
            uuid = UUID.randomUUID(),
            uuidList = listOf(UUID.randomUUID()),
            valid = random.nextBoolean()
        )
    }
}